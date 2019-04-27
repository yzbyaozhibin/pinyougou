/** 定义控制器层 */
app.controller('indexController', function($scope, baseService){
    /** 定义获取登录用户名方法 */
    $scope.showName = function(){
        baseService.sendGet("/user/showName")
            .then(function(response){
                $scope.loginName = response.data.loginName;
                $scope.findUserByLoginName();
            });
    };
    $scope.findUserByLoginName = function () {
        baseService.sendGet("/user/findUserByUsername?username=" + $scope.loginName)
            .then(function (response) {
                $scope.entity = response.data.user;
                $scope.entity.birthday=response.data.birthdayString;
                $scope.entity.address = JSON.parse($scope.entity.address);
            });
    };
});