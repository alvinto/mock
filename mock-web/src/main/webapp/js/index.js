
var mockall = angular.module("mockall", [ 'ngDialog', 'ngRoute']);

mockall.config(function ($routeProvider) {
    $routeProvider.when("/index", {
        templateUrl: contextPath+"/templates/index.html",
        controller: "index"
    }).otherwise("/index");
});

mockall.controller("index", function ($scope,$http,ngDialog) {
    var dubboList = [];
    var restList = [];
    /**
     * keyword 查询关键字
     * currentpage 当前页数
     * type 0：dubbo；1：rest
     */
    queryList = function(keyword,currentpage,type){
    	var json = {};
    	json.type = type;
    	json.pageNum = currentpage;
    	if(keyword == undefined){
    		keyword = "";
    	}
    	json.keyword = keyword;
    	json.username = $("#currentUser").val();
    	$http({
            url: "conf/query_list_by_type",
            method: "POST", 
            data:JSON.stringify(json),
            cache: false
        }).success(function(data) {
    		if(type == 0){
    			$scope.list = data.data;
    			dubboList = $scope.list;
    			pageController(data.totalCnt,data.pageSize,data.pageNum,keyword,type);
    		}else if(type == 1){
    			$scope.restlist = data.data;
    			restList = $scope.restlist;
    			pageController(data.totalCnt,data.pageSize,data.pageNum,keyword,type);
    		}
        });
    };
    
    queryList('','1',0);
    queryList('','1',1);

    $scope.queryDubboList = function(){
    	queryList($scope.dubbokey,'1',0);
    };
    
    $scope.queryRestList = function(){
    	queryList($scope.restkey,'1',1);
    };
       
    $scope.removeList = function(index){
        ngDialog.confirm({
            title:"删除确认框" ,
            content:"是否确认删除？",
            onOk:function(){
            	$scope.obj = {};
                $scope.obj = dubboList[index];
                $scope.obj.username = $("#currentUser").val();
                $http({
                    url: "conf/remove_dubbo",
                    method: "POST",
                    data: JSON.stringify($scope.obj),
                    cache: false
                }).success(function(data) {
                	ngDialog.alert({
                        title:"提示",
                        content:"删除成功！"
                    });
                	queryList("","1",0);
                });
            },
            onCancel:function(){}
        });
    };

    $scope.editDubbo = function(index){
        var isEdit = false;
        var oldUrl = "";
        if(typeof index != 'undefined'){
            isEdit = true;
            oldUrl = dubboList[index].facadeName +"."+dubboList[index].methodName;
        }
        ngDialog.editor({
            template:contextPath+"/templates/dubbo-edit.html",
            title:isEdit ? "编辑" :"新增" ,
            controller:['$scope',function($scope){
                $scope.obj = {};
                if(isEdit){
                    $scope.obj = dubboList[index];
                    $scope.obj.oldUrl = oldUrl;
                }
                $scope.responseTip = false;
                $scope.requestTip = false;
                $scope.obj.username = $("#currentUser").val();
                $scope.submit = function(){
                	if($scope.responseTip || $scope.requestTip){
                		return;
                	}
                    $http({
                        url: "conf/put_dubbo",
                        method: "POST",
                        data: JSON.stringify($scope.obj),
                        cache: false
                    }).success(function(data) {
                    	//$scope.obj = data;
                    	$scope.closeThisDialog();
                    	var con = isEdit ? "更新成功！" :"添加成功！";
                    	ngDialog.alert({
                            title:"提示",
                            content:con
                        });
                    	queryList("","1",0);
                    });

                };
                $scope.cancel = function(){
                    $scope.closeThisDialog();
                };
                
                changeTip = function(name,value){
                	if(name == "requestTip"){
                		$scope.requestTip = value;
                	}
                	if(name == "responseTip"){
                		$scope.responseTip = value;
                	}
                };
                
                getJsonStr = function (name){
                	if(name == "requestTip"){
                		return $scope.obj.request;
                	}
                	if(name == "responseTip"){
                		return $scope.obj.response;
                	}
                };
                
                $scope.validatorJson = function(name){
                	var jsonStr = getJsonStr(name);
                	try{
                		if(jsonStr == undefined || jsonStr == ""){
                			changeTip(name,false);
                		}else{
                			if(isNaN(jsonStr)){
                				JSON.parse(jsonStr);
                				changeTip(name,false);
                			}else{
                				changeTip(name,true);
                			}
                		}
            		}catch(message){
            			changeTip(name,true);
            		}
            		
                };
            }]

        });

    };

    $scope.editRest = function(index){
    	var isEdit = false;
    	var oldUrl = "";
        if(typeof index != 'undefined'){
            isEdit = true;
            oldUrl =restList[index].restMethod + " " + restList[index].url;
        }
        ngDialog.editor({
            template:contextPath+"/templates/rest-edit.html",
            title:isEdit ? "编辑" :"新增" ,
            controller:['$scope',function($scope){
            	$scope.obj = {};
                if(isEdit){
                    $scope.obj = restList[index];
                    $scope.obj.oldUrl = oldUrl;
                }else{
                	$scope.obj.restMethod = "post";
                }
                $scope.obj.username = $("#currentUser").val();
                $scope.requestHeadersTip = false;
                $scope.requestTip = false;
                $scope.responseHeadersTip = false;
                $scope.responseTip = false;
                $scope.submit = function(){
                	if(($scope.requestHeadersTip || $scope.requestTip
                			|| $scope.responseHeadersTip || $scope.responseTip)){
                		return;
                	}
                    $http({
                        url: "conf/put_rest",
                        method: "POST",
                        data: JSON.stringify($scope.obj),
                        cache: false
                    }).success(function(data) {
                    	$scope.closeThisDialog();
                        $scope.obj = data;
                        if(data.code == 0){
                        	var con = isEdit ? "更新成功！" :"保存成功！";
                        	ngDialog.alert({
                                title:"提示",
                                content:con
                            });
                        	queryList("","1",1);
                        }
                    });

                };
                
                $scope.cancel = function(){
                    $scope.closeThisDialog();
                };
                
                changeTip = function(name,value){
                	if(name == "requestHeadersTip"){
                		$scope.requestHeadersTip = value;
                	}
                	if(name == "requestTip"){
                		$scope.requestTip = value;
                	}
                	if(name == "responseHeadersTip"){
                		$scope.responseHeadersTip = value;
                	}
                	if(name == "responseTip"){
                		$scope.responseTip = value;
                	}
                };
                
                getJsonStr = function (name){
                	if(name == "requestHeadersTip"){
                		return $scope.obj.requestHeaders;
                	}
                	if(name == "requestTip"){
                		return $scope.obj.request;
                	}
                	if(name == "responseHeadersTip"){
                		return $scope.obj.responseHeaders;
                	}
                	if(name == "responseTip"){
                		return $scope.obj.response;
                	}
                };
                
                $scope.validatorJson = function(name){
                	var jsonStr = getJsonStr(name);
                	
                	try{
                		if(jsonStr == undefined || jsonStr == ""){
                			changeTip(name,false);
                		}else{
                			if(isNaN(jsonStr)){
                				JSON.parse(jsonStr);
                				changeTip(name,false);
                			}else{
                				changeTip(name,true);
                			}
                		}
            		}catch(message){
            			changeTip(name,true);
            		}
            		
                };
            }]
        });
    };
    
    $scope.removeRestList = function(index){
    	ngDialog.confirm({
            title:"删除确认框" ,
            content:"是否确认删除？",
            onOk:function(){
            	$scope.obj = {};
            	$scope.obj = restList[index];
            	$scope.obj.username = $("#currentUser").val();
                $http({
                    url: "conf/remove_rest",
                    method: "POST",
                    data: JSON.stringify($scope.obj),
                    cache: false
                }).success(function(data) {
                	ngDialog.alert({
                        title:"提示",
                        content:"删除成功！"
                    });
                	queryList("","1",1);
                });
            },
            onCancel:function(){}
        });
    };
    
    $scope.restore = function(index,type){
    	ngDialog.confirm({
            title:"还原确认框" ,
            content:"是否确认还原为原始版本？",
            onOk:function(){
            	if(type == 0){
            		var url = "dubbo";
            		$scope.obj = {};
            		$scope.obj = dubboList[index];
            		console.log($scope.obj);
            		if($scope.obj != null){
            			url = url + "/" + $scope.obj.facadeName + "/" + $scope.obj.methodName;
            		}
            		$http({
            			url: url,
            			method: "delete",
            			cache: false
            		}).success(function(data) {
            			ngDialog.alert({
            				title:"提示",
            				content:data.resultData
            			});
            		});
            	}
            	if(type == 1){
            		var url = "rest";
            		$scope.obj = {};
            		$scope.obj = restList[index];
            		console.log($scope.obj);
            		if($scope.obj != null){
            			url = url + "/" + $scope.obj.restMethod + "~~" + $scope.obj.url;
            		}
            		console.log(url);
            		$http({
            			url: url,
            			method: "delete",
            			cache: false
            		}).success(function(data) {
            			ngDialog.alert({
            				title:"提示",
            				content:data.resultData
            			});
            		});
            	}
            },
            onCancel:function(){}
        });
    };
    
    $scope.callRest = function(index) {
    	$scope.obj = {};
        $scope.obj = restList[index];
        var url = contextPath+'/mock/'+$("#currentUser").val()+$scope.obj.url;
    	$.ajax({
    		'type': $scope.obj.restMethod,
    		'contentType':$scope.obj.requestHeaders.contentType,
    		'url': url,
    		'success': function(data,status,xhr){
            	ngDialog.editor({
                    template:contextPath+"/templates/simulate.html",
                    controller:['$scope',function($scope){
                    	$scope.obj = {};
                    	$scope.obj.response = data;
                    	$scope.obj.url = url;
                    	$scope.obj.responseHeaders = xhr.getAllResponseHeaders();
                        $scope.cancel = function(){
                            $scope.closeThisDialog();
                        };
                    }]
                });
    		},
    		'error': function(){
    			ngDialog.alert({
                    title:"提示",
                    content:"失败"
                });
    		}
    	});
    };
    
    $scope.callDubbo = function(index) {
    	var temp = dubboList[index];
        var url = contextPath+'/dubbo/'+temp.facadeName+"/"+temp.methodName;
        $http({
            url: url,
            method: "POST",
            data: temp.request,
            cache: false
        }).success(function(data) {
        	ngDialog.editor({
                template:contextPath+"/templates/dubbo-mock.html",
                controller:['$scope',function($scope){
                	$scope.obj = {};
                	$scope.obj.facadeName = temp.facadeName;
                	$scope.obj.methodName = temp.methodName;
                	$scope.obj.request = temp.request;
                	$scope.obj.response = data;
                    $scope.cancel = function(){
                        $scope.closeThisDialog();
                    };
                }]
            });
        });
    };
    
    $scope.test = function() {
        var url = 'http://192.168.1.159:8787/mtp-web'+'/dubbo/ws/com.ws.Test/doGet';
        var temp = {};
        temp.request = '{"id":"1"}';
        console.log(encodeURIComponent(url));
        $http({
            url: url,
            method: "delete",
            data: temp,
            cache: false
        }).success(function(data) {
        	ngDialog.editor({
                
            });
        });
    };
    
    $scope.exportFile = function(index,type) {
    	var temp;
    	if(type == 0){
    		temp = dubboList[index];
    	}
    	if(type == 1){
    		temp = restList[index];
    	}
    	temp.type = type;
        $http({
            url: contextPath+'/conf/download',
            method: "POST",
            data: JSON.stringify(temp),
            cache: false
        }).success(function(data, status, headers) {
            var octetStreamMime = 'application/octet-stream';
            // Get the headers
            headers = headers();
            var temp = headers['content-disposition'];
            var ts = temp.split(";");
            // Get the filename from the x-filename header or default to "服务列表"
            var filename =ts[2].split("=")[1];
            filename = filename.substring(1,filename.length-1);
            console.log("===="+filename+"=====");
            // Determine the content type from the header or default to "application/octet-stream"
            var contentType = headers['content-type'] || octetStreamMime;


            try
            {
                // Try using msSaveBlob if supported
                console.log("Trying saveBlob method ...");
                var blob = new Blob([data], {type: contentType});
                saveAs(blob, filename);
//                if(navigator.msSaveBlob)
//                	window.navigator.msSaveBlob(blob, filename);
//                else {
//                    // Try using other saveBlob implementations, if available
//                    var saveBlob = window.navigator.webkitSaveBlob || window.navigator.mozSaveBlob || window.navigator.saveBlob;
//                    if(saveBlob === undefined) throw "Not supported";
//                    saveBlob(blob, filename);
//                }
//                console.log("saveBlob succeeded");
//                success = true;
            } catch(ex)
            {
                console.log("saveBlob method failed with the following exception:");
                console.log(ex);
            }
        });
    };
    
//    $scope.importFile = function(){
//    	var url = contextPath+'/conf/upload';
//    	console.log($scope.file);
//    	var blob = new Blob([$scope.file], {type: 'multipart/form-data'});
//    	console.log(blob);
//    	$http({
//            url: url,
//            method: "POST",
//            data: blob,
//            cache: false
//        }).success(function(data) {
//        	
//        });
//    }
    
    $scope.uploadFile = function () {
    	var file = $scope.file;
        Upload.upload({
            url: 'upload/url',
            data: {file: file}
        }).then(function (resp) {
            console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
        }, function (resp) {
            console.log('Error status: ' + resp.status);
        }, function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
        });
    };

});