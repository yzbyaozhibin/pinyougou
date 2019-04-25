// 定义购物车的控制器
app.controller('orderController', function ($scope, $controller, $interval, $location, baseService) {
    // 继承cartController
    $controller('cartController', {$scope : $scope});
    $scope.addressSave={notes:[]};
    // 获取收件人地址列表
    $scope.findAddressByUser = function () {
        // 发送异步请求
        baseService.sendGet("/order/findAddressByUser").then(function(response){
            // 获取响应数据
            $scope.addressList = response.data;

            // 获取默认的收件地址
            $scope.address = $scope.addressList[0];
        });
    };

    // 用户选择地址
    $scope.selectedAddress = function (item) {
        $scope.address = item;
    };

    // 判断是否为选中的收件地址
    $scope.isSelectedAddress = function (item) {
        return $scope.address == item;
    };

    /** 显示修改 */
    $scope.show = function (item) {
        /** 把json对象转化成一个新的json对象*/
        $scope.addressSave= JSON.parse(JSON.stringify(item));
    }

    // 定义数据封装的json对象
    $scope.order = {paymentType : '1'};

    // 支付方式选择
    $scope.selectPayType = function (payType) {
        $scope.order.paymentType = payType;
    };

    // 提交订单
    $scope.saveOrder = function () {
        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.address.contact;
        // 订单来源
        $scope.order.sourceType = "2";

        // 发送异步请求
        baseService.sendPost("/order/save", $scope.order).then(function(response){
            // 获取响应数据
            if (response.data){
                // 如果支付方式是在线支付
                if ($scope.order.paymentType == 1){
                    // 跳转到支付页面
                    location.href = "/order/pay.html";
                }else{
                    // 跳转到成功页面
                    location.href = "/order/paysuccess.html";
                }
            }else{
                alert("提交订单失败！");
            }
        });
    };


    // 生成微信支付二维码
    $scope.genPayCode = function () {
        baseService.sendGet("/order/genPayCode").then(function(response){
            // 获取响应数据  response.data : {outTradeNo: '', money : 1, codeUrl : ''}
            // 获取交易订单号
            $scope.outTradeNo = response.data.outTradeNo;
            // 获取支付金额
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            // 获取支付URL
            $scope.codeUrl = response.data.codeUrl;

            // 支付二维码
            document.getElementById("qrcode").src = "/barcode?url=" + $scope.codeUrl;

            // 开启定时器
            // 第一个参数：定时需要回调的函数
            // 第二个参数：间隔的时间毫秒数 3秒
            // 第三个参数：总调用次数 100
            var timer = $interval(function(){

                // 发送异步请求，获取支付状态
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo).then(function(response){
                    // 获取响应数据: response.data: {status : 1|2|3} 1:支付成功 2：未支付 3:支付失败
                    if (response.data.status == 1){// 支付成功
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付成功的页面
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if (response.data.status == 3){
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付失败的页面
                        location.href = "/order/payfail.html";
                    }
                });
            }, 3000, 100);

            // 总调用次数结束后，需要调用的函数
            timer.then(function(){
                // 关闭订单
                $scope.tip = "二维码已过期，刷新页面重新获取二维码。";
            });

        });
    };


    // 获取支付的总金额
    $scope.getMoney = function () {
        return $location.search().money;
    };

    $scope.js=function (index) {
        if (index === $scope.addressList.length-1){
            $(".address").hover(function(){
                $(this).addClass("address-hover");
            },function(){
                $(this).removeClass("address-hover");
            });
            $("body").show();
        }
    };

    /** 选择地址别名 */
    $scope.selectAlias=function (alias) {
        $scope.addressSave.alias=alias;
    }


});