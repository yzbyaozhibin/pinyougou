/** 定义控制器层 */
app.controller('settingController', function($scope, $location, baseService){
    $scope.entity = {nickName:null,sex:null,birthday:null,address:null,job:null,headPic:null};
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

    $scope.saveOrUpdate = function(entity){
        baseService.sendPost("/user/saveUser",$scope.entity)
            .then(function (response) {
                $scope.entity = response.data;
                $scope.findUserByLoginName();
        });

    };

    $scope.provincesList = {};
    $scope.citiesList = {};
    $scope.areasList = {};
    $scope.selectProvincesList = function () {
        baseService.sendGet("/user/findProvincesList").then(function (response) {
            $scope.provincesList = response.data;
        });
    };

    $scope.$watch('entity.address.province', function (newVal, oldVal) {
        if (newVal){ // 不是undefined、null
            baseService.sendGet("/user/findCitiesList?provinceId=" + $scope.entity.address.province).then(function (response) {
                $scope.citiesList = response.data;
            });
        }else {
            $scope.citiesList = [];
        }
    });
    $scope.$watch('entity.address.city', function (newVal, oldVal) {
        if (newVal){ // 不是undefined、null
            baseService.sendGet("/user/findAreasList?cityId=" + $scope.entity.address.city).then(function (response) {
                $scope.areasList = response.data;
            });
        }else {
            $scope.areasList = [];
        }
    });

    // 定义文件上传
    $scope.uploadFile = function () {
        // 调用服务层上传文件
        baseService.uploadFile().then(function(response){
            // 获取响应数据 {status : 200, url : 'http://192.168.12.131/group1/xxx/xx/x.jpg'}
            if(response.data.status == 200){
                // 获取图片url $scope.picEntity : {url : '', color:''}
                $scope.entity.headPic = response.data.url;
                $scope.saveOrUpdate();
                alert("上传成功！")
            }else{
                alert("上传失败！");
            }
        });
    };

    $scope.jobsList = [{name:"程序猿"},{name:"设计狮"},
                       {name:"产品狗"},{name:"教师"},
                       {name:"医生"},{name:"其它"}];

    $scope.selectJob=function (job) {
        $scope.entity.job=job;
    }

});