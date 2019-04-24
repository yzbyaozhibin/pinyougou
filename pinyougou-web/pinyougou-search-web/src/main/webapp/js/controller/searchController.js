/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope, baseService,$location) {
    $scope.search = {pageSize:10,criteria:'',spec:{},sort:{}};
    $scope.updateSearch=function (key,value, b) {
        if (b == 1){
            $scope.search.spec[key] = value;

        }else if (b == 2){
            $scope.search.sort={};
            $scope.search.sort[key] = value;
        }else {
            $scope.search[key] = value;
        }
        $scope.searchPage(1);
    };
    $scope.deleteSearch=function (key, b) {
        if (b == 1){
            $scope.search.spec[key]=null;
        }else if (b == 2){
            $scope.search.sort={};
        }else {
            $scope.search[key] = null;
        }
        $scope.searchPage(1);
    };
    $scope.searchPage=function (pageNum) {
        $scope.search.pageNum = parseInt(pageNum);
        baseService.sendPost("/Search",$scope.search).then(function (response) {
            $scope.itemList=response.data.itemList;
            $scope.page=response.data.page;
            $scope.categorySet=response.data.categorySet;
            $scope.brandSet=response.data.brandSet;
            $scope.specMap=response.data.specMap;
            $scope.pageFor=[];
            $scope.d = $scope.page.current;
        },function (response) {
            alert("失败");
        });
    };

    $scope.$watch("page",function () {
        if ($scope.page){
            for (var i = $scope.page.start; i <= $scope.page.end;i++){
                $scope.pageFor.push(i);
            }
        }
    });
    $scope.acceptSearch=function () {
        var search = $location.search();
        if (search.keyword){
            $scope.search.criteria=search.keyword;
        }
    }

});
