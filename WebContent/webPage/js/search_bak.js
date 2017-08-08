var currentFile;
var attribute = new Array();
var attrColumns = [];
var tag="y";
var counter=1;
$(document).ready(function(){
	//工具栏隐藏
	$("#toolbar,#tableHideDiv").css("display","none");
	
});

//单条搜索
$("#searchBtn").click(function(){
	var keys=new Array();
	var values=new Array();
    keys.push($("#singleSelectOpts").val());
	values.push($("#singleAttrInput").val());
	console.log("keys "+keys);	
	console.log("values "+values);
	if(keys!=null&&keys!=""&&values!=null&&values!=""){
	   startSearch(keys, values);
	}
});

//开始搜索
function startSearch(keys, values){
    tag="y";
    counter=1;
	searchResult(keys, values,"default");
};
//获取搜索结果
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
	        	if(data.msg!=null){
	        		$('#resultsTable').bootstrapTable('destroy');
	        		$('ul.menu-nav').find("li").remove();
	        		$("#toolbar,#tableHideDiv").css("display","none");
	        		alert(data.msg);
	        	}else{
		        	if(counter<2){
		        		$(".weblct-sidemenu .menu-nav").empty();
		        		$.each(data.files, function(i, f) {
							$(".weblct-sidemenu .menu-nav").append(
									'<li  title="'+f+'" style="padding: 0px 0px;"><a style="padding:5px 5px" href="#">'+f+'</a></li>'
									);});
			        	
			        	$(".weblct-sidemenu .menu-nav li").bind("click",function(){
			        		var file=$(this).text();
			        		console.log(file);
	                        counter++;
	                        tag="n";//不在搜索？
	                        searchResult(keys, values,file);
			        	});
		        	}
		        	$("#addItemForm").empty();
		           //attrColumns表的属性列
		            attrColumns.splice(0,attrColumns.length);
		            var t= {field: "del", title: "del", align: "center"};
					attrColumns.push(t);
					var t0 = {field: "id", title: "id", align: "center"};
					attrColumns.push(t0);
					
					$.each(data.attributes, function(i, item) {
						if (item!="子路径"){
							var temp = {field: item, title: item, align: "center"};//手动拼接columns
			                attrColumns.push(temp);
			                
			                var formItem = '<div class="form-group">'+
			                	                 '<label for="formItem'+i+'" class="col-sm-4 control-label">'+ item+ '</label>'+
								                 ' <div class="col-sm-8">'+
								                          '<input type="text" class="form-control" id="formItem'+i+'" name="'+item+ '" placeholder="请输入'+ item+ '">'+
								                 ' </div>' +
							        	   '</div>'
						$("#addItemForm").append(formItem);
						$("#addItemForm .form-group").first()
								.css("display", "none");
						}
	
					});
					t1 = {field: "operate", title: "操作", align: "center"};
					attrColumns.push(t1);
					$("#toolbar,#tableHideDiv").css("display","block");
					changeTable();
	        	}
	        }
	    });
	};
	var height;
	function changeTable() {
		height=$(window).height()-120;
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
						
							smartDisplay:false,
							// content-Type:"application/json;charset=UTF-8",
							sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
							pageNumber : 1, // 初始化加载第一页，默认第一页
							pageSize : 10, // 每页的记录行数（*）
							pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
							useCurrentPage:true,
							// smartDisplay: false,//智能显示分页按钮
							//search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
							strictSearch : true,
							showColumns : true, // 是否显示所有的列
							showRefresh : true, // 是否显示刷新按钮
							minimumCountColumns : 2, // 最少允许的列数
							clickToSelect : true, // 是否启用点击选中行
							height : height,// 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
							uniqueId : "id", // 每一行的唯一标识，一般为主键列
							showToggle : true, // 是否显示详细视图和列表视图的切换按钮
							cardView : false, // 是否显示详细视图
							detailView : true, // 是否显示详细信息
							columns: attrColumns,
							detailFormatter : function(index, row) {
								var $html = $('<div class=\"detailPage\"></div>');
								$.each(row,function(key, value) {
									
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
		
		tableCss();

	};
	$(window).resize(function() {
		$('#tableHideDiv').height($(window).height()-120);
		console.log("height1"+$('#tableHideDiv').height());
		height=$('#tableHideDiv').height();
		$table.bootstrapTable('resetView', {
		  	height: height
		  	});
		});
	
