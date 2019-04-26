package com.pinyougou.smsMicroservice.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsListener {
    @Autowired
    private Environment evn;

    private static final String PRODUCT = "Dysmsapi";
    // 产品域名,开发者无需替换
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    @JmsListener(destination = "sprintBoot.sms")
    public void sendSms(Map map){
        // 签名KEY
        String accessKeyId =evn.getProperty("sms.accessKeyId");;
        // 签名密钥
        String accessKeySecret = evn.getProperty("sms.accessKeySecret");
        String phone = (String) map.get("phone");
        String signName = (String) map.get("signName");
        String templateCode = (String) map.get("templateCode");
        String templateParam = (String) map.get("templateParam");
        try{

            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            // 组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            // 必填:待发送手机号
            request.setPhoneNumbers(phone);
            // 必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            // 可选:模板中的变量替换JSON串,如模板内容为: 验证码${number}，您正进行身份验证，打死不告诉别人！
            request.setTemplateParam(templateParam);

            // hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println(sendSmsResponse);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
