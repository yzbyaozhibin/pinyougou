/** 定义控制器层 */
app.controller('userController', function($scope, $timeout,$interval, baseService,$controller){
    $controller('indexController',{$scope:$scope});
    // 定义user对象
    $scope.user = {};
    /** 用户注册 */
    $scope.save = function () {

        // 判断两次密码是否一致
        if ($scope.okPassword && $scope.user.password == $scope.okPassword){
            // 发送异步请求
            baseService.sendPost("/user/save?code=" + $scope.code, $scope.user)
                .then(function(response){
                if (response.data){
                    // 清空表单数据
                    $scope.user = {};
                    $scope.okPassword = "";
                    $scope.code = "";
                }else{
                    alert("注册失败！");
                }
            });

        }else{
            alert("两次密码不一致！");
        }
    };




    // 发送短信验证码
    $scope.sendSmsCode = function () {

        // 判断手机号码
        if ($scope.user.phone && /^1[3|4|5|7|8]\d{9}$/.test($scope.user.phone)){
            // 发送异步请求
            baseService.sendGet("/user/sendSmsCode?phone=" + $scope.user.phone)
                .then(function(response){
                if (response.data){
                    // 调用倒计时方法
                    $scope.downcount(90);

                }else{
                    alert("发送失败！");
                }
            });
        }else {
            alert("手机号码格式不正确！")
        }
    };


    $scope.smsTip = "获取短信验证码";
    $scope.disabled = false;

    // 倒计时方法
    $scope.downcount = function (seconds) {

        seconds--;

        if (seconds >= 0){
            $scope.smsTip = seconds + "秒后，重新获取！";
            $scope.disabled = true;
            // 第一个参数：回调的函数
            // 第二个参数：间隔的时间毫秒数
            $timeout(function(){
                $scope.downcount(seconds);
            }, 1000);
        }else {
            $scope.smsTip = "获取短信验证码";
            $scope.disabled = false;
        }

    };


    //判断用户原密码是否正确
    $scope.changePassword=function () {
        if($scope.user.newPassword == $scope.rePassword && $scope.user.newPassword){
            if ($scope.user.oldPassword ){
                baseService.sendPost("/user/codePassword",$scope.user).then(function (response) {
                    if (response.data == true){
                        alert("修改成功");
                        $scope.user={};
                        $scope.rePassword="";
                    }else {
                        $("#oldPasswordWarn").html("原密码错误!");
                    }
                })
            }else {
                $("#oldPasswordWarn").html("密码不能为空!");
            }
        }else {
            $("#rePasswordWarn").html("修改的密码有误!");
            return;
        }
    };
    // 获取收件人地址列表
    $scope.findAddressByUser = function () {
        // 发送异步请求
        baseService.sendGet("/address/findAddressByUser").then(function(response){
            // 获取响应数据
            $scope.addressList = response.data;

            // 获取默认的收件地址
            $scope.address = $scope.addressList[0];
        });
    };

    //显示详细地址
    $scope.showAddress=function (item) {
        $scope.addressSave=item;
    };
    $scope.changeAlias=function (alias) {
        $scope.addressSave.alias=alias;
    };
    //根据父级id查询地区
    $scope.findAddressByParentId=function () {
        baseService.sendGet("/user/findAddressByParentId").then(function (response) {
            $scope.Provinces = response.data;
        })
    };

   
   $scope.$watch("addressSave.provinceId",function (newValue,oldValue) {
        if($scope.addressSave){
            baseService.sendGet("/user/findCitiesByProvinceId?provinceId="+newValue).then(function (response) {
                $scope.cities = response.data;
            })
        }
    });

    $scope.$watch("addressSave.cityId",function (newValue,oldValue) {
        if($scope.addressSave){
            baseService.sendGet("/user/findAreasByCityId?cityId="+newValue).then(function (response) {
                $scope.areas = response.data;

            })
        }
    });


    /*######################### 陈志杨的代码##################################*/
    $scope.time = 0;
    //获取手机号
    $scope.findPhone=function () {
        baseService.sendGet("/user/findUserPhoneByUserId").then(function (response) {
            $scope.phone = response.data;

        })
    };

    //发送验证码
    $scope.sendCode=function () {
        if(!$scope.vcode){
            $("#codeWarn").html("验证码不能为空!");
            return;
        }
        baseService.sendGet("/user/judgeCode?code="+$scope.vcode).then(function (response) {
            if (response.data){
                baseService.sendGet("/user/sendCode?phone="+$scope.phone).then(function (response) {
                    if (response.data){
                        $scope.timer();
                    }
                });
            }else {
                $("#codeWarn").html("验证码不正确!");
            }
        })
    };

    //清空验证码提醒信息
    $scope.changeVCodeWarn=function () {
        $("#codeWarn").html("");
    };
    //清空短信验证码提醒信息
    $scope.changeSmsCodeWarn=function () {
        $("#smsCodeWarn").html("");
    };

    //定时器
    $scope.timer=function () {
        $scope.time = 60;
        $interval(function () {
            $scope.time = $scope.time - 1;
        },1000,60);
    };

    //下一步
    $scope.next1=function () {
        if(!$scope.code){
            $("#smsCodeWarn").html("短信验证码不能为空!");
            return;
        }
        baseService.sendGet("/user/checkSmsCode?phone="+$scope.phone+"&code="+$scope.code).then(function (response) {
            if (response.data){
                location.href="home-setting-address-phone.html";
            }else {
                $("#smsCodeWarn").html("短信验证码不正确!");
            }
        })
    };

    $scope.next2=function () {
        if(!$scope.code){
            $("#smsCodeWarn").html("短信验证码不能为空!");
            return;
        }
        baseService.sendGet("/user/updatePhone?phone="+$scope.phone+"&code="+$scope.code).then(function (response) {
            if (response.data.success){
                location.href="home-setting-address-complete.html";
            }else {
                $("#smsCodeWarn").html(response.data.message);
            }
        })
    };

    //查询有订单
    $scope.findAllOrder=function () {
        baseService.sendGet("/order/findOrders").then(function (response) {
            $scope.orders=response.data;
        })
    };

    //支付款
    $scope.pay=function(orderMap){
        baseService.sendPost("/order/pay?totalFee="+orderMap.totalFee,orderMap.order).then(function (response) {
            if (response.data){
                location.href="http://cart.pinyougou.com/order/pay.html";
            }else {
                alert("异常")
            }
        })
    };
//修改默认地址
    $scope.updateDefaultAddress=function (item) {
        var i =  $scope.addressList[0];
        i.isDefault = "0";
        // alert(JSON.stringify(i));
        baseService.sendPost("/user/updateAddress",i);
        item.isDefault ="1";
        // alert(JSON.stringify(item))
        baseService.sendPost("/user/updateAddress",item);
    }
});