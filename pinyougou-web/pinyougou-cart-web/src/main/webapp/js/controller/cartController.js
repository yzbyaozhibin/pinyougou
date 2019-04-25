// 定义购物车的控制器
app.controller('cartController', function ($scope, $controller, baseService) {

    // 继承baseController
    $controller('baseController', {$scope : $scope});

    // 定义提交购物车订单集合
    $scope.orderCarts=[];

    //往购物车订单里加订单
    $scope.addOrderCart=function (x,y,event) {
        var cart = $scope.isExistSeller($scope.orderCarts,$scope.carts[x]);
        if (cart){
            if (event.target.checked){
                cart.orderItems.push($scope.carts[x].orderItems[y]);
            }else {
                var index = cart.orderItems.indexOf($scope.carts[x].orderItems[y]);
                cart.orderItems.splice(index,1);
                if (cart.orderItems.length<=0){
                    var i = $scope.orderCarts.indexOf(cart);
                    $scope.orderCarts.splice(i,1);
                }
            }
        } else {
            cart=JSON.parse(JSON.stringify($scope.carts[x]));
            cart.orderItems=[];
            cart.orderItems.push($scope.carts[x].orderItems[y]);
            $scope.orderCarts.push(cart);
        }
    };

    //商家全选
    $scope.sellerSelect=function (x,event) {
        var ev = {target:{checked:false}};
        if(event.target.checked){
            ev.target.checked=true;
            var cart = $scope.isExistSeller($scope.orderCarts,$scope.carts[x])
            if (cart){
                var ind = $scope.orderCarts.indexOf(cart);
                $scope.orderCarts.splice(ind,1);
            }
        }
        for(var i = 0;i < $scope.carts[x].orderItems.length;i++){
            $scope.addOrderCart(x,i,ev);
        }
        $.each($("input[name="+x+"]"),function (k,v) {
            if(event.target.checked){
                $(v).prop("checked",true);
            }else {
                $(v).prop("checked",false);
            }
        });
    };

    //判断是够已存在此商家
    $scope.isExistSeller=function (carts,cart) {
        for(var i = 0;i < carts.length;i++){
            if (carts[i].sellerName == cart.sellerName){
                return carts[i];
            }
        }
        return null;
    };

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


