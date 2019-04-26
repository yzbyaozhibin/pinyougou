// 定义购物车的控制器
app.controller('cartController', function ($scope, $controller, baseService) {

    // 继承baseController
    $controller('baseController', {$scope: $scope});



    $scope.findCartList = function () {
        baseService.sendGet("/cart/findCart").then(function (response) {
            $scope.carts = response.data;
            $scope.totalNum = 0;
            $scope.totalPrice = 0;
            $scope.sum($scope.carts);
        });
    };
    $scope.findCarOrderList = function () {
        baseService.sendGet("/cart/findCartOrders").then(function (response) {
            $scope.carts = response.data;
            $scope.totalNum = 0;
            $scope.totalPrice = 0;
            $scope.sum($scope.carts);
        });
    };
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart?itemId="
            + itemId + "&num=" + num).then(function (response) {
            $scope.carts = response.data;
            $scope.findCartList();
        });
    };
    $scope.focus = function (num) {
        $scope.i = num;
    };
    $scope.sum = function (carts) {
        for (var i = 0; i < carts.length; i++) {
            var orderItems = carts[i].orderItems;
            for (var j = 0; j < orderItems.length; j++) {
                $scope.totalNum += orderItems[j].num;
                $scope.totalPrice += orderItems[j].totalFee;
            }
        }
    };


    /*############################# 陈志杨代码#####################################*/

    // 定义提交购物车订单集合
    $scope.orderCarts = [];

    //定义总金额
    $scope.stf="0.00";

    //定义存储删除订单列表
    $scope.deletedOrderItems=[];

    //往购物车订单里加订单
    $scope.addOrderCart = function (x, y, event) {
        var cart = $scope.isExistSeller($scope.orderCarts, $scope.carts[x]);
        if (cart) {
            if (event.target.checked) {
                cart.orderItems.push($scope.carts[x].orderItems[y]);
            } else {
                var index = cart.orderItems.indexOf($scope.carts[x].orderItems[y]);
                cart.orderItems.splice(index, 1);
                if (cart.orderItems.length <= 0) {
                    var i = $scope.orderCarts.indexOf(cart);
                    $scope.orderCarts.splice(i, 1);
                }
            }
        } else {
            cart = JSON.parse(JSON.stringify($scope.carts[x]));
            cart.orderItems = [];
            cart.orderItems.push($scope.carts[x].orderItems[y]);
            $scope.orderCarts.push(cart);
        }
        $scope.sumOrder();
    };

    //商家全选
    $scope.sellerSelect = function (x, event) {
        var ev = {target: {checked: false}};
        if (event.target.checked) {
            ev.target.checked = true;
            var cart = $scope.isExistSeller($scope.orderCarts, $scope.carts[x]);
            if (cart) {
                var ind = $scope.orderCarts.indexOf(cart);
                $scope.orderCarts.splice(ind, 1);
            }
        }
        for (var i = 0; i < $scope.carts[x].orderItems.length; i++) {
            $scope.addOrderCart(x, i, ev);
        }
        $.each($("input[name=" + x + "]"), function (k, v) {
            if (event.target.checked) {
                $(v).prop("checked", true);
            } else {
                $(v).prop("checked", false);
            }
        });
        $scope.sumOrder();
    };

    //全选
    $scope.selectAll = function (event) {

        var ev = {target: {checked: false}};
        if (event.target.checked) {
            ev.target.checked = true;
            $scope.orderCarts = [];
        }
        for (var i = 0; i < $scope.carts.length; i++) {
            $scope.sellerSelect(i, ev);

        }
        $scope.sumOrder();
    };


    //判断是够已存在此商家
    $scope.isExistSeller = function (carts, cart) {
        for (var i = 0; i < carts.length; i++) {
            if (carts[i].sellerName == cart.sellerName) {
                return carts[i];
            }
        }
        return null;
    };
    //算价格
    $scope.sumOrder=function () {
        $scope.tf=0;
        for (var i = 0; i < $scope.orderCarts.length; i++) {
            for (var j = 0; j < $scope.orderCarts[i].orderItems.length; j++) {
                $scope.tf+=$scope.orderCarts[i].orderItems[j].totalFee;
            }
        }
        $scope.stf=""+$scope.tf;
        $scope.stf=$scope.stf.substring(0,$scope.stf.indexOf(".")+3);
    };

    //结算
    $scope.addCartOrders = function () {
        if ($scope.orderCarts == null || $scope.orderCarts.length <1  ){
            alert("请选择要结算的商品");
            return;
        }
        baseService.sendPost("/cart/addCartOrders",$scope.orderCarts).then(function (response) {
            if (response.data.success){
                location.href="/order/getOrderInfo.html";
            }else {
                alert(response.data.message);
            }
        });
    };
    //单个删除
    $scope.deleteOne=function (itemId,num,item) {
        $scope.addCart(itemId,num);
        $scope.deletedOrderItems.push(item);
    };

    //清除选中的订单
    $scope.deleteSelected=function () {
        var maps = [];
        for (var i = 0; i < $scope.orderCarts.length; i++) {
            for (var j = 0; j < $scope.orderCarts[i].orderItems.length; j++) {
                $scope.tf+=$scope.orderCarts[i].orderItems[j].totalFee;
                maps.push({"itemId":""+$scope.orderCarts[i].orderItems[j].itemId,"num":""+$scope.orderCarts[i].orderItems[j].num});
                $scope.deletedOrderItems.push($scope.orderCarts[i].orderItems[j]);
            }
        }
        baseService.sendPost("/cart/deleteSelected",maps).then(function (response) {
            if(response.data){
                $scope.findCartList();
            }
        });
        $.each($("input[type=checkbox]"), function (k, v) {
                $(v).prop("checked", false);
        });
        $scope.orderCarts = [];
        $scope.stf="0.00";
    };

    //重新购买
    $scope.buyAgain=function (itemId,num,index) {
        $scope.addCart(itemId,num);
        $scope.deletedOrderItems.splice(index,1);
    }
});


