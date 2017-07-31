<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/> 
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>Search tools</title>
<link rel="stylesheet" href="webPage/css/bootstrap.css">
<link rel="stylesheet" href="webPage/css/bootstrap-table.css">

<script src="webPage/js/jquery-2.1.1.min.js"></script>
<script src="webPage/js/bootstrap.min.js"></script>

<script src="webPage/js/bootstrap-table.min.js"></script>
<script src="webPage/js/bootstrap-table-zh-CN.js"></script>

<script src="webPage/js/refresh.js"></script>

<link rel="stylesheet" href="webPage/css/index.css"> 
</head>
<body scroll="no" >

<nav class="navbar navbar-default weblct-nav" role="navigation">
    <div class="container-fluid"> 
	    <div class="navbar-header">
	        <a class="navbar-brand" href="#">WeblctSearch</a>
	    </div>
    <div class="pull-right">
        <form class="navbar-form navbar-right search-nav" role="search">

	          <div class="form-group">
		              <div class="input-group">
							<div class="input-group-btn">
								<button type="button" class="btn btn-default dropdown-toggle search-attr" data-toggle="dropdown">
									<span id="attrSelect" class="attr-text">属性选择</span>									
									<span class="caret"></span>
								</button>
								<ul class="dropdown-menu">									
								</ul>
							</div>
							<input type="text" class="form-control attrInput" placeholder="Search">
					</div>
	         </div>
             <button id="searchBtn" type="submit" class="btn btn-default">搜索</button>
             <a class="advancedSearch" data-toggle="modal" data-target="#searchModal" >高级检索</a>
             <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				    <div class="modal-dialog">
				        <div class="modal-content">
				            <div class="modal-header">
				                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				                <h4 class="modal-title" id="myModalLabel">高级搜索</h4>
				            </div>
				            <div class="modal-body">
			                 <!--    <div style="background-color:#F5F5F5"><p>添加查询项</p></div>	 -->        
						        <div class="add-search" style="">
							        <div id="addSearchForm" class="form-group">
						                <div class="input-group" style="inline-block">
												<div class="input-group-btn">
													<button type="button" class="btn btn-default dropdown-toggle search-attr" data-toggle="dropdown">
														<span id="attrText" class="attr-text">属性选择</span>									
														<span class="caret"></span>
													</button>
													<ul class="dropdown-menu">														
													</ul>
												</div>
												<input  type="text" class="form-control attrInput" placeholder="">	
									    </div>
									    <button type="button" class="btn btn-default" onclick="addSearchFunc()">添加检索项</button>
									    <span id="alertMsg" style="color:red;display:none;font-style:italic"></span>
	                                 </div>
                                 </div>	           
								<table id="searchTable" class="table table-bordered">
									  <thead>
									      <tr><th class="col-lg-3 col-md-3 col-xs-3 col-sm-3">检索属性</th>
									          <th class="col-lg-7 col-md-7 col-xs-7 col-sm-7">检索值</th>
									          <th class="col-lg-2 col-md-2 col-xs-2 col-sm-2">操作</th>
									      </tr>
									  </thead>
									  <tbody></tbody>
								</table>	                                     								  			            
				            </div>
				            <div class="modal-footer">
				                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				                <button type="button" class="btn btn-primary" onclick="queryDeal()">查询</button>
				            </div>
				        </div>
				    </div>
			</div>
        </form>
    </div>
    </div>
</nav>

<div class="container main-panel">
<!-- 检索部分 -->
  <div class="row">  
     <div class="sidebar-nav weblct-sidemenu">
		     <div class="panel panel-default" style="overflow:auto">
				    <div class="panel-heading">
				        <h6 class="panel-title">
				                                        检索结果文件列表
				        </h6>
				    </div>
				    <div style="padding:0px;margin-top: 40px;" class="panel-body">
				         <ul class="nav nav-pills nav-stacked menu-nav">
						        <!--  <li  title="totalresourcestotalresources.xls:sheet0" class="" style="padding: 0px 0px;"><a class="atextFormat" href="#">totalresourcestotalresources.xls:sheet0</a></li>
		                         <li class="" style="padding: 0px 0px;"><a class="atextFormat" href="#">totalresources.xls:sheet0</a></li>
		                         <li class="" style="padding: 0px 0px;"><a class="atextFormat" href="#">totalresources.xls:sheet0</a></li> -->

		                 </ul>
		                 <div style="position: absolute;"></div>
				    </div>
		    </div>        
      </div> 
      
<!-- 检索结果部分 -->
	   <div class=" searchResult">    
	       <div id="toolbar">
			   <button id="remove" class="btn btn-sm " onclick="deleteItems()" style="margin-left:10px">
		               <i class="glyphicon glyphicon-remove"></i> 删除
		        </button>
				<button id="addBtn" class="btn btn-sm " data-toggle="modal" data-target="#addModal" style="margin-left:10px">
		                <i class="glyphicon glyphicon-plus"></i>添加
		        </button>
		        <span><input class="pull-right" type="file" id="inputfile"></span>
		        <!-- 模态框（Modal） -->
		        
					<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"  data-backdrop="static">
					    <div class="modal-dialog">
					        <div class="modal-content">
					            <div class="modal-header">
					                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                <h4 class="modal-title" id="myModalLabel">添加数据</h4>
					            </div>
					            
					            <div  class="modal-body"  >
					            <form class="form-horizontal" role="form" id="addItemForm">
					            </form>									                
					            </div>
					            
					            <div class="modal-footer">
					                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					                <button type="button" class="btn btn-primary" onclick="addItem()" >提交</button>
					            </div>
					             
					        </div><!-- /.modal-content -->
					    </div><!-- /.modal -->
					</div>
				
			 </div> 
	       <table class="table" id="resultsTable" data-unique-id=data-index; >
	           <thead>
	               <tr>
	                   <!-- <th data-checkbox="true">选择</th> -->
	               </tr>
	           </thead>
	       </table>
	  </div>
	   </div>
	   </div>
<script src="webPage/js/fileDirectory.js"></script>

<script src="webPage/js/detailDeal.js"></script>
<script src="webPage/js/selectChange.js"></script><!-- 会不会覆盖之前原生的方法？ 要不要用监听器？-->
<script src="webPage/js/search.js"></script>
<script src="webPage/js/addOperation.js"></script>
<script src="webPage/js/delectOperation.js"></script>



</body>
</html>
