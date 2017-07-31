<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=10"/> 

<title>Search tools</title>
<link rel="stylesheet" href="webPage/css/bootstrap.css">
<link rel="stylesheet" href="webPage/css/bootstrap-table.css">

<script src="webPage/js/jquery-2.1.1.min.js"></script>
<script src="webPage/js/bootstrap.min.js"></script>

<script src="webPage/js/bootstrap-table.min.js"></script>
<script src="webPage/js/bootstrap-table-zh-CN.js"></script>

<link rel="stylesheet" href="webPage/css/homePage.css"> 

</head>
<body>
<div id="header">
  <span class="glyphicon glyphicon-search themeIcon">WeblctSearch</span>
</div>

<div class="container mainPanel">
  <!-- 上传部分 --> 
  <form id= "uploadForm">
  <br>
      <input type="file" name="myfile">
      <input type="button" value="上传" onclick="doUpload()">
  </form>  
<!-- 检索部分 -->
  <div class="row searchIndex">        
    <p style="padding-top:1%"> 
        <span style="font-weight:bold;font-style:italic;margin-left:3%">请输入检索条件：</span>
        <span id="alertMsg" style="color:red;display:none;font-style:italic"></span>
        
    </p>
    <div class="operationInput col-lg-2 col-md-2 col-sm-2">
         <button id="addInput" type="button" class="btn btn-default btn-sm operationBtn pull-right" onclick="addFormItem()">
		     <span class="glyphicon glyphicon-plus"></span>
	     </button>
	     <button id="reduceInput" type="button" class="btn btn-default btn-sm operationBtn pull-right" onclick="removeFormItem()">
		     <span class="glyphicon glyphicon-minus"></span>
	     </button>
    </div>
    <div class="col-lg-10 col-md-10 col-sm-10">
       <form  id="searchForm" class="bs-example bs-example-form " role="form">
          <div id="formItem0" class="form-group col-lg-6 col-md-6 col-sm-6">
              <div class="input-group" >
                  <div class="input-group-btn searchInput" >     
                        <select class="btn btn-default" style="margin:0px;border:none" name="modelId">  
                        </select>             
                       <!-- <select  class="btn btn-default" style="margin:0px;border:none" >
                            <option value="zhFuncEntryName" selected="selected">中文功能入口名</option>
                            <option value="enFuncEntryName" >英文功能入口名</option> 
                            <option value="functionId">功能ID</option>
                            <option value="commandWordId">命令字ID</option>
                            <option value="bookname">bookname</option>
                            <option value="factory">厂家告警原始名称</option>
                            <option value="name">名字</option>
                            <option value="data">数据</option>
                            <option value="nodeAttr">节点属性</option>
                            
				       </select>			 -->       
			      </div>			     
                  <input type="text" class="form-control"  placeholder="请输入检索关键字" >
              </div>
         </div>
       </form>
       
       
       <button class="btn btn-default btn-sm pull-right operationBtn" id="resetBtn" onclick="resetAll()">
              <span class="glyphicon glyphicon-trash">&nbsp;重置</span>
       </button>  
       <button type="submit" name="submit" class="btn btn-default btn-sm pull-right operationBtn" id="searchBtn" onclick="startSearch()">
              <span class="glyphicon glyphicon-search">&nbsp;查询</span>
       </button>  
       
     </div>
  </div>
      
      
<!-- 检索结果部分 -->
   <div class="row searchResult">    
       <div id="toolbar">
		   <button id="remove" class="btn btn-sm " onclick="deleteItems()" style="margin-left:10px">
	               <i class="glyphicon glyphicon-remove"></i> 删除
	        </button>
			<button id="addBtn" class="btn btn-sm " data-toggle="modal" data-target="#addModal" style="margin-left:10px">
	                <i class="glyphicon glyphicon-plus"></i>添加
	        </button>
	        <!-- 模态框（Modal） -->
	        
				<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" >
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
                   <th data-checkbox="true">选择</th>
               </tr>
           </thead>
       </table>
  </div>
</div>










<script src="webPage/js/fileDirectory.js"></script>
<script src="webPage/js/detailDeal.js"></script>
<script src="webPage/js/selectChange.js"></script><!-- 会不会覆盖之前原生的方法？ 要不要用监听器？-->
<script src="webPage/js/searchIndex.js"></script>
<script src="webPage/js/addOperation.js"></script>
<script src="webPage/js/delectOperation.js"></script>
<script src="webPage/js/bootstrap-table-toolbar.js"></script>


</body>
</html>
