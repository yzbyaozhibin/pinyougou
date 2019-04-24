// 定义购物车的控制器
app.controller('cartController', function ($scope, $controller, baseService) {

    // 继承baseController
    $controller('baseController', {$scope : $scope});

    $scope.findCartList=function () {
        baseService.sendGet("/cart/findCart").then(function (response) {
            $scope.carts = response.data;
            $scope.totalNum=0;
            $scope.totalPrice=0;
            $scope.sum($scope.carts);
        });
    };
    $scope.addCart=function (itemId,num) {
        baseService.sendGet("/cart/addCart?itemId="
            + itemId + "&num=" + num).then(function (response) {
            $scope.carts = response.data;
            $scope.findCartList()
        });
    };
    $scope.focus=function (num) {
        $scope.i = num;
    };
    $scope.sum=function (carts) {
        for(var i = 0;i<carts.length;i++){
            var orderItems = carts[i].orderItems;
            for(var j = 0;j<orderItems.length;j++){
                $scope.totalNum += orderItems[j].num;
                $scope.totalPrice += orderItems[j].totalFee;
            }
        }
    };
});


