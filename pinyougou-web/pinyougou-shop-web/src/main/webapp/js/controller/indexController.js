// 定义首页控制器
app.controller('indexController', function ($scope, baseService) {

    // 获取登录用户名
    $scope.showLoginName = function () {
        // 发送异步请求
        baseService.sendGet("/showLoginName").then(function(response){
            // 获取用户名
            $scope.loginName = response.data.loginName;
        });
    };
});