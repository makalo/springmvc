var currentFile;
var attribute = new Array();
var attrColumns = [];
var tag="y";
var counter=1;
$(document).ready(function(){
	/*location.reload();*/
	//获取所有属性值
	$.ajax({
		type : "GET",
		url : "getAllAttributes",
		async: false,  
		dataType : "json",
		success : function(data) {
			attribute = data;
			$.each(attribute, function(i, item) {
					$(".dropdown-menu").append(
							'<li><a href="#">'
									+ item
									+ '</a></li>');
				
			});
		}
	});
	//下拉菜单值的选取
	$(".search-nav .dropdown-menu li").bind("click",function(){
		var attr=$(this).text();
		$(this).parents(".input-group").find(".attr-text").text(attr);
		$(this).parents(".input-group").find("input").val("");
		console.log(attr);
	});

	//输入搜索条件校验
	$(".attrInput").bind("blur",function(){
		if($(this).val()==""||$(this).val()==null){
			$(this).parents('.form-group').addClass("has-error");
			$("#alertMsg").text("检索条件不能为空");
	 		$("#alertMsg").css("display","inline-block");
	 		
		} else{
			$(this).parents('.form-group').removeClass("has-error");
			$("#alertMsg").text("");
	 		$("#alertMsg").css("display","none");
		}      
	}); 
	//当modal关闭时，对元素内容清空
	$('#searchModal').on('hide.bs.modal', function () {
		$('#searchModal').find("input").val("");
		$("#searchTable tbody").html("");
		$("#attrText").text("属性选择");
		$("#alertMsg").text("");
 		$("#alertMsg").css("display","none");
 		if($("#addSearchForm").hasClass("has-error")){
 			$("#addSearchForm").removeClass("has-error");
 		}
})

});

//单条搜索
$("#searchBtn").click(function(){
	var keys=new Array();
	var values=new Array();
    keys.push($("#attrSelect").text());
	values.push($(".search-nav input").val());
	console.log("keys "+keys);	
	console.log("values "+values);
	startSearch(keys, values);
});

//将增加的搜索条件加入表格中
function addSearchFunc(){
	var key=$("#attrText").text();
	var value=$("#addSearchForm .attrInput").val();
	
	if(key!="属性选择"&&value!=""&&value!=null){
		$("#alertMsg").css("display","none");	
		
		if(!$("#addSearchForm").hasClass("has-error")){
			$("#searchTable tbody").append('<tr ><td class="col-lg-3 col-md-3 col-xs-3 col-sm-3">'+
					key+'</td><td class="col-lg-7 col-md-7 col-xs-7 col-sm-7">'+value+'</td>'+
					'</td><td class="col-lg-2 col-md-2 col-xs-2 col-sm-2"><span class="delSearchSpan" style="color:#59B8EA" onclick="delectSearchRow(this)">删除</span></td>'+
					'</tr>');
			
		}	
	}else if(key=="属性选择"){
		$("#alertMsg").text("请选择要查询的属性");
 		$("#alertMsg").css("display","inline-block");
	}else if(value==""||value==null){
		$("#alertMsg").text("检索条件不能为空");
 		$("#alertMsg").css("display","inline-block");
 		$("#addSearchForm").addClass("has-error");
	}
};
//删除某一行搜索对
function delectSearchRow(trSpan){
	var $trSpan=$(trSpan);
	$trSpan.parents("tr").remove();
	
}
//高级搜索数据处理
function queryDeal(){
	var keys=new Array();
	var values=new Array();
	var searchTable=$("#searchTable");
	$("#searchTable tr td:nth-child(1)").each(function () {
		keys.push($(this).text());
    });
	$("#searchTable tr td:nth-child(2)").each(function () {
		values.push($(this).text());
    });
	console.log("keys "+keys);	
	console.log("values "+values);
	//开始搜索
	if(keys.length!=0){
		$('#searchModal').modal('hide')
	    startSearch(keys,values);
	}else{
		$("#alertMsg").text("请在表格中添加检索项");
 		$("#alertMsg").css("display","inline-block");
	}
	
};
function startSearch(keys, values){
    tag="y";
    counter=1;
	searchResult(keys, values,"default");
};
function searchResult(keys, values,file) {
	
	console.log(keys+" "+values);
	var data2={"keys":keys.join("."),"values":values.join("."),"flag":"a","tag":tag,"file":file};
	    $.ajax({
	        url: "getlists",
	        type: "get",
	        dataType: "json",
	        data:data2,
	        async: false, 
	        success: function (data) {
	        	if(counter<2){
	        		$(".weblct-sidemenu .menu-nav").empty();
	        		$.each(data.files, function(i, f) {
						$(".weblct-sidemenu .menu-nav").append(
								'<li  title="'+f+'" style="padding: 0px 0px;"><a href="#">'+f+'</a></li>'
								);});
		        	
		        	$(".weblct-sidemenu .menu-nav li").bind("click",function(){
		        		var file=$(this).text();
		        		console.log(file);
                        counter++;
                        tag="n";
                        searchResult(keys, values,file);
		        	});
	        	}
	        	
	           attrColumns.splice(0,attrColumns.length);
	           var t= {field: "del", title: "del", align: "center"};
				attrColumns.push(t);
				var t0 = {field: "id", title: "id", align: "center"};
				attrColumns.push(t0);
				$("#addItemForm").empty();
				$.each(data.attributes, function(i, item) {
					if (item!="子路径"){
						var temp = {field: item, title: item, align: "center"};//手动拼接columns
		                attrColumns.push(temp);
		                
		                var formItem = '<div class="form-group">'
							+ '<label for="formItem'
							+ i
							+ '" class="col-sm-4 control-label">'
							+ item
							+ '</label>'
							+ ' <div class="col-sm-8">'
							+ '<input type="text" class="form-control" id="formItem'
							+ i
							+ '" name="'
							+item
							+ '" placeholder="请输入'
							+ item
							+ '">'
							+ ' </div>' + '</div>'
					$("#addItemForm").append(formItem);
					$("#addItemForm .form-group").first()
							.css("display", "none");
					}

				});
				t1 = {field: "operate", title: "操作", align: "center"};
				attrColumns.push(t1);
				changeTable();
	        }
	    });
	};
	function changeTable() {
		$('#resultsTable').bootstrapTable('destroy');
		$('#resultsTable').bootstrapTable(
						{
							url : "getlists", // 请求后台的URL（*）
							method : 'get', // 请求方式（*）
							toolbar : '#toolbar', // 工具按钮用哪个容器
							cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
							pagination : true, // 是否显示分页（*）
							paginationLoop : true,
							sortable : false, // 是否启用排序
							sortOrder : "asc", // 排序方式
							// content-Type:"application/json;charset=UTF-8",
							sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
							pageNumber : 1, // 初始化加载第一页，默认第一页
							pageSize : 10, // 每页的记录行数（*）
							pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
							// smartDisplay: false,//智能显示分页按钮
							search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
							strictSearch : true,
							showColumns : true, // 是否显示所有的列
							showRefresh : true, // 是否显示刷新按钮
							minimumCountColumns : 2, // 最少允许的列数
							clickToSelect : true, // 是否启用点击选中行
							height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
							uniqueId : "id", // 每一行的唯一标识，一般为主键列
							showToggle : true, // 是否显示详细视图和列表视图的切换按钮
							cardView : false, // 是否显示详细视图
							detailView : true, // 是否显示详细信息
							columns: attrColumns,
							detailFormatter : function(index, row) {
								var $html = $('<div class=\"detailPage\"></div>');
								$.each(row,function(key, value) {
									console.log(
key);
													if (key == "路径") {
														$html.append('<p class="location" style="display:none"><b>'
																		+ key
																		+ ':</b> '
																		+ '<span>'
																		+ value
																		+ '</span>'
																		+ '</p>');
													}
													if (key != 0 && key != "路径" && key!="子路径" && key!="del") {
														$html.append('<p><span class=\"enEditSpan\"><span class="keySpan"><b>'
																		+ key
																		+ ':</b></span> '
																		+ '<span class=\"valueSpan\">'
																		+ value
																		+ '</span>'
																		+ '</span></p>');
													}
												})
								$html.append('<button onclick="enbleEdit(this,'
										+ index + ')">编辑</button>');
								$html.append('<button onclick="enbleSave(this,'
										+ index + ')">保存</button>');
								$html.append('<span class="msgSpan" style="display:none;color:#337AB7;margin-left:10px">保存</span>');
								return $html;
							},

							responseHandler : function(data) {
								currentFile=data.rows[0].路径;
								console.log(currentFile);
								return {
									"total" : data.total,// 总页数
									"rows" : data.rows, // 数据
								};
							},
							
							queryParams : function queryParams(params) { // 设置查询参数
								var param = {
									pageNumber : params.offset + 1,
									pageSize : params.limit,
									flag:"b"

								};
								return param;
							},

							onLoadSuccess : function(data) { // 加载成功时执行
								console.log("加载成功");
								console.log(data.rows);
							},
							onLoadError : function(data) { //加载失败时执行  
								console.log("加载数据失败");
							}

						});

		

	};
	function doUpload() {
		var formData = new FormData($("#uploadForm")[0]);
		$.ajax({
			url : "upload",
			type : 'POST',
			data : formData,
			async : false,
			cache : false,
			contentType : false,
			processData : false,
			success : function(returndata) {
				alert("上传成功");
			},
			error : function(returndata) {
				//alert(returndata);
				alert("上传成功");
			}
		});
	}
