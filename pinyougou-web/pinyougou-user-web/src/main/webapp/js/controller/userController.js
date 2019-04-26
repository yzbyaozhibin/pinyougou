/** 定义控制器层 */
app.controller('userController', function($scope, $timeout, baseService,$controller){
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
        if($scope.addressSave.provinceId){
            baseService.sendGet("/user/findCitiesByProvinceId?provinceId="+newValue).then(function (response) {
                $scope.cities = response.data;
            })
        }
    });

    $scope.$watch("addressSave.cityId",function (newValue,oldValue) {
        if($scope.addressSave.cityId){
            baseService.sendGet("/user/findAreasByCityId?cityId="+newValue).then(function (response) {
                $scope.areas = response.data;

            })
        }
    })
});