<%--
  Created by IntelliJ IDEA.
  User: kevin
  Date: 2016/5/19
  Time: 18:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html ng-app="mockall">
  <head>
    <title>DubboMock</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@include file="common/header.jsp"%>
    <script type="text/javascript">
      var contextPath = "${pageContext.request.contextPath}";
    </script>
    <style type="text/css">
      /*.word-break {
        word-wrap: break-word;
        word-break: break-all;
      }*/
      table{
        table-layout:fixed;
      }
      * {
        font-size: 13px;
        font-family: 微软雅黑;
      }
      td {
        text-align:center; /*设置水平居中*/
        vertical-align:middle;/*设置垂直居中*/
        white-space:nowrap;
        overflow:hidden;
        text-overflow: ellipsis;
      }
      .text-center{
        text-align: center;
        vertical-align: middle;
      }
    </style>
  </head>
  <body>
  <div style="float:right">
	  	选择用户：
	  <select id="username">
	  	<option value="default" selected>default</option>
	  	<option value="app">app</option>
	  	<option value="h5">h5</option>
	  </select>
	  <button type="button" class="btn btn-primary btn-sm btn-rest" onclick="changeUser()">切换</button>
	  当前用户：<input id="currentUser" type="text" value="default" style="border-style:none"/>
  </div>
  <div class="container">
    <div class="row">
      <div class="col-lg-12" ng-view>

      </div>
    </div>
  </div>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/index.js"></script>
  <script type="text/javascript">
  	changeUser();
	function changeUser(){
		$("#currentUser").val($("#username").val());
		queryList('','1',0);
		queryList('','1',1);
		/**
		$.ajax({
    		'type': "post",
    		'url': contextPath+"/conf/login",
			'data':$("#username").val(),
			'contentType':"text/html; charset=utf-8",
    		'success': function(data,status,xhr){
				$("#currentUser").val($("#username").val());
				queryList('','1',0);
				queryList('','1',1);
            	ngDialog.alert({
                    title:"提示",
                    content:"切换成功！"
                });
    		},
    		'error': function(){
    			ngDialog.alert({
                    title:"提示",
                    content:"失败"
                });
    		}
    	});
    	*/
	}
  </script>
  </body>
</html>
