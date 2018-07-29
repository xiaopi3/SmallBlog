<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="zh">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>个人日记本主页</title>
<link href="${pageContext.request.contextPath}/style/blog.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/bootstrap/js/jQuery.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js"></script>
<style type="text/css">
	  body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
</style>
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">屌丝的日记本</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li class="active"><a href="#"><i class="icon-home"></i>&nbsp;主页</a></li>
              <li class="active"><a href="#"><i class="icon-pencil"></i>&nbsp;写日记</a></li>
              <li class="active"><a href="#"><i class="icon-book"></i>&nbsp;日记分类管理</a></li>
              <li class="active"><a href="#"><i class="icon-user"></i>&nbsp;个人中心</a></li>
            </ul>
          </div>
          <form name="myForm" class="navbar-form pull-right" method="post" action="">
			  <input class="span2" id="s_title" name="s_title"  type="text" style="margin-top:5px;height:30px;" placeholder="往事如烟...">
			  <button type="submit" class="btn" onkeydown="if(event.keyCode==13) myForm.submit()"><i class="icon-search"></i>&nbsp;搜索日志</button>
		  </form>
        </div>
      </div>
</div>

<div class="container">
	<div class="row-fluid">
	  <div class="span9">
	  
		  <jsp:include page="${mainDiary }"></jsp:include>
		  <% System.out.println(request.getAttribute("mainDiary"));%>
		  
	  </div>
	  <div class="span3">
	  
	  	<div class="data_list">
		  	<div class="data_list_title">
				<img src="${pageContext.request.contextPath}/images/user_icon.png"/>
				个人中心
			</div>
		  </div>
		  
		  <div class="data_list">
		  	<div class="data_list_title">
				<img src="${pageContext.request.contextPath}/images/byType_icon.png"/>
				日志类别
			</div>
		  </div>
		  
		  <div class="data_list">
		  	<div class="data_list_title">
				<img src="${pageContext.request.contextPath}/images/byDate_icon.png"/>
				日志日期
			</div>
		  </div>
		  
	  </div>
	</div>
</div>

</body>
</html>