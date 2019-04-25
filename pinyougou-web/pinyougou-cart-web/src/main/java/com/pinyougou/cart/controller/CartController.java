package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-18<p>
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 10000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /** 把SKU商品添加到购物车 */
    @GetMapping("/addCart")
    // SpringMVC跨域请求的注解
    @CrossOrigin(origins = {"http://item.pinyougou.com"},
            allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num){

        // 设置允许哪些域名可以跨域访问 99%
        //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
        // 设置允许哪些域名可以跨域操作Cookie 1%
        //response.setHeader("Access-Control-Allow-Credentials", "true");

        try{
            // 获取登录用户名
            String userId = request.getRemoteUser();

            // 1. 获取该用户原来的购物车
            List<Cart> carts = findCart();

            // 2. 添加SKU商品到购物车
            carts = cartService.addItemToCart(carts, itemId, num);

            if (StringUtils.isNoneBlank(userId)){ // 已登录
                /** ############已登录用户，把购物车存储到Redis数据库########### */
                cartService.saveCartRedis(userId, carts);


            }else{ // 未登录
                /** ############未登录用户，把购物车存储到Cookie中########### */
                CookieUtils.setCookie(request, response,
                        CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(carts),
                        60 * 60 * 24, true);
            }

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查看用户的购物车 */
    @GetMapping("/findCart")
    public List<Cart> findCart(){

        List<Cart> carts = null;
        // 获取登录用户名
        String userId = request.getRemoteUser();

        if (StringUtils.isNoneBlank(userId)){ // 已登录
            /**############## 已登录用户从Redis数据库获取购物车数据 ################*/
            carts = cartService.findCartRedis(userId);

            /**############ 购物车数据合并 ################*/
            // 1. 获取Cookie中的购物车数据
            String cartJsonStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isNoneBlank(cartJsonStr)){
                // 把cartJsonStr字符串转化List<Cart>
                List<Cart> cookieCarts = JSON.parseArray(cartJsonStr, Cart.class);
                if (cookieCarts.size() > 0){
                    // 2. 把cookieCarts合并到carts中
                    carts = cartService.mergeCart(cookieCarts, carts);

                    // 3. 把合并后的购物车重新存储到Redis
                    cartService.saveCartRedis(userId, carts);

                    // 4. 从Cookie中删除购物车
                    CookieUtils.deleteCookie(request, response,
                            CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }


        }else{ // 未登录
            /**############## 未登录用户从Cookie中获取购物车数据 ################*/
            // List<Cart> : [{},{}]
            String cartJsonStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);

            // 判断购物车是否存在
            if (StringUtils.isBlank(cartJsonStr)){
                // 创建新的购物车
                cartJsonStr = "[]";
            }

            // 把cartJsonStr字符串转化List<Cart>
            carts = JSON.parseArray(cartJsonStr, Cart.class);
        }
        return carts;
    }
    @PostMapping("/addCartOrders")
    public Map<String,Object> addCartOrders(@RequestBody List<Cart> cartOrders){
        Map<String,Object> map = new HashMap<>();
        try {
            String userId = request.getRemoteUser();
            List<Cart> carts = findCart();
            cartService.saveCartOrdersToRedis(userId,cartOrders,carts);
            map.put("success",true);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("success",false);
        map.put("message","网络异常");
        return map;
    }
    @GetMapping("/findCartOrders")
    public List<Cart> findCartOrders(){
        try {
            String userId = request.getRemoteUser();
            return cartService.findCartOrdersFromRedis(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
