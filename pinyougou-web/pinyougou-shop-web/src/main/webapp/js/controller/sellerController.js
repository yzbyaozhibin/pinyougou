/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 添加或修改  商家注册*/
    $scope.saveOrUpdate = function(){
        /** 发送post请求 */
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function(response){
                if (response.data){
                    /** 跳转到登录页面 */
                    location.href = "/shoplogin.html";
                }else{
                    alert("操作失败！");
                }
            });
    };

//商家用户信息修改
    $scope.update =function () {
        /** 发送post请求 */
        baseService.sendPost("/seller/update", $scope.seller).then(function (response) {
            if (response.data) {
                alert("修改成功！");
                $scope.show();
            } else {
                alert("操作失败！");
            }
        });
    };



    /** 显示修改 */
    $scope.show = function(entity){
        baseService.sendGet("/seller/findOne").then(function (response) {
                $scope.seller = response.data;
            /** 把json对象转化成一个新的json对象 */
            $scope.seller = JSON.parse(JSON.stringify(seller));
        })
    };



    /** 查询条件对象 */
    $scope.searchEntity = {};

    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/seller/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/seller/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };

    //定义一个空的usre对象
    $scope.user={};
    $scope.changePassword = function () {
        if($scope.user.newPassword && $scope.user.rePassword){
            if ($scope.user.newPassword != $scope.user.rePassword){
                alert("输入的两次密码前后不一致!!!");
                $scope.user.newPassword = "";
                $scope.user.rePassword  = "";
                return;
            }else {
                if ($scope.user.oldPassword){
                    baseService.sendPost("/seller/changePassword",$scope.user).then(function (response) {
                        if (response.data){
                            alert("修改成功!");
                            $scope.user.oldPassword = "";
                            $scope.user.newPassword = "";
                            $scope.user.rePassword  = "";
                            location.href="http://shop.pinyougou.com/logout";

                        }else {
                            alert("输入的原密码有误,请重新输入");
                            $scope.user.oldPassword = "";
                            $scope.user.newPassword = "";
                            $scope.user.rePassword  = "";
                        }
                    });
                }else {
                    alert("请输入原密码!")
                }
            }
        }else {
            alert("输入的两次密码前后不一致!!!");
        }

    };

    //重置密码
    $scope.reset = function () {
        $scope.user.oldPassword = "";
        $scope.user.newPassword = "";
        $scope.user.rePassword  = "";
    };


    //文本1框显示密码与隐藏
    $scope.showpwd1 = function () {
        var pwdNum1 =document.getElementById("oldPassword");
        var icon1 =document.getElementById("icon1");
        if (pwdNum1.getAttribute("type") == "password"){
            pwdNum1.setAttribute("type","text");
            icon1.setAttribute("class","fa fa-eye-slash");
            icon1.style.color="red";
        }else {
            pwdNum1.setAttribute("type","password");
            icon1.setAttribute("class","fa fa-eye");
            icon1.style.color="blue";
        }
    };

    //文本2框显示密码与隐藏
    $scope.showpwd2 = function () {
        var pwdNum2 =document.getElementById("newPassword");
        var icon2 =document.getElementById("icon2");
        if (pwdNum2.getAttribute("type") == "password"){
            pwdNum2.setAttribute("type","text");
            icon2.setAttribute("class","fa fa-eye-slash");
            icon2.style.color="red";
        }else {
            pwdNum2.setAttribute("type","password");
            icon2.setAttribute("class","fa fa-eye");
            icon2.style.color="blue";
        }
    };

    //文本3框显示密码与隐藏
    $scope.showpwd3 = function () {
        var pwdNum3 =document.getElementById("rePassword");
        var icon3 =document.getElementById("icon3");
        if (pwdNum3.getAttribute("type") == "password"){
            pwdNum3.setAttribute("type","text");
            icon3.setAttribute("class","fa fa-eye-slash");
            icon3.style.color="red";
        }else {
            pwdNum3.setAttribute("type","password");
            icon3.setAttribute("class","fa fa-eye");
            icon3.style.color="blue";
        }
    };
});