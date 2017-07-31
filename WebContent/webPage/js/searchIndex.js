var $formItems = $("#searchForm");
var attribute = new Array();
var questionColumns = [];

// var $formItems;
// 加入检索条件输入框
function addFormItem() {
	var idNum = $(".form-group").length;
	if (idNum < 4) {
		var $cloneForm = $("#formItem0").clone(true);
		$cloneForm.attr('id', 'formTtem' + idNum);
		$cloneForm.attr('class', "form-group col-lg-6 col-md-6 col-sm-6");
		$cloneForm.find("input").val("");
		// $cloneForm.find("select").attr('selectedIndex', 0);
		$formItems.append($cloneForm);
	} else {
		$("#alertMsg").text("检索条件不要大于4个");
		$("#alertMsg").css("display", "inline-block");
	}

}
// 删除检索条件输入框
function removeFormItem() {
	console.log($(".form-group").length);
	if ($(".form-group").length >= 4) {
		$("#alertMsg").css("display", "none")
	}
	if ($(".form-group").length > 1) {
		$formItems.children(".form-group:last").remove();
	}
}
// 重置所有检索条件输入框
function resetAll() {
	var $formGroups = $("#searchForm .form-group");
	$formGroups.find("input").val("");
	$formGroups.find("select").each(function() {
		$(this).val("zhFuncEntryName");
	})
}
// 对检索条件进行校验
$(document).ready(function() {
					var legal = true;
					$formItems.find("input").bind("blur",function() {
								if ($(this).val() == ""|| $(this).val() == null) {
									$(this).parents('.form-group').addClass("has-error");
									$("#alertMsg").text("检索条件不能为空");
									$("#alertMsg").css("display","inline-block");
								}
								if (($(this).val().length < 3)
										&& ($(this).val().length > 0)) {
									$(this).parents('.form-group').addClass("has-error");
									$("#alertMsg").text("输入检索条件的字数大于3");
									$("#alertMsg").css("display","inline-block");

								}
							});
					$formItems.find("input").bind(
									"input propertychange",
									function() {
										var legal = true;
										var illegalChars = "!~^><.:$#【】";
										var inputArray = $(this).val().split("");
										for (var i = 0; i < inputArray.length; i++) {
											if (illegalChars.indexOf(inputArray[i]) >= 0) {
												legal = false;
											}
										}
										if (!legal) {
											$(this).parents('.form-group').addClass("has-error");
											$("#alertMsg").text("出现非法字符!~^><.:$#【】");
											$("#alertMsg").css("display","inline-block");
										}

										if ($(this).val().length >= 3 && legal) {
											$(this).parents('.form-group').removeClass("has-error");
											$("#alertMsg").css("display","none");
										}
									});
					$.ajax({
								type : "GET",
								url : "getAllAttributes",
								// data: {username:$("#username").val(),
								// content:$("#content").val()},
								dataType : "json",
								success : function(data) {
									attribute = data;
									$.each(attribute, function(i, item) {
										if (item == "中文功能入口名") {
											$("select[name=modelId]").append(
													"<option value=item selected=selected>"
															+ item
															+ "</option>");
										} else {
											$("select[name=modelId]").append(
													"<option value=item>"
															+ item
															+ "</option>");
										}
									});
								}
							});
				})
function startSearch() {
	var flag = true;
	var $formGroups = $("#searchForm .form-group");
	$formGroups.each(function() {
		if ($(this).hasClass("has-error")) {
			flag = false;
		}
		;
	})
	if (!flag) {
		alert("请输入正确的检索条件");
	} else {
		$("#alertMsg").css("display", "none");
		var keys = new Array();
		var values = new Array();
		var nullIndex = "";
		$("#searchForm input").each(function(index, element) {
			if ($(this).val() != "" && $(this).val() != null) {
				values.push($(this).val());
			} else {
				nullIndex = nullIndex + index;
			}
		})
		$("#searchForm select").each(function(index, element) {
			if (nullIndex.indexOf(index) == -1) {
				keys.push($(this).find("option:selected").text());
			}
		})

		
		
		searchResult(keys, values);
		//changeTable(keys, values);
	}
};
function searchResult(keys, values) {
var data2={"keys":keys.join("."),"values":values.join("."),"flag":"a"};
console.log("tttt");
    $.ajax({
        url: "getlists",
        type: "get",
        dataType: "json",
        data:data2,
        success: function (data) {
        	console.log("kkkkk");
           var t= {field: "del", title: "del", align: "center"};
			questionColumns.push(t);
			var t0 = {field: "id", title: "id", align: "center"};
			questionColumns.push(t0);
			$("#addItemForm").empty();
			$.each(data, function(i, item) {
				var temp = {field: item, title: item, align: "center"};//手动拼接columns
                questionColumns.push(temp);
                
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
			
			
			});
			t1 = {field: "operate", title: "操作", align: "center"};
			questionColumns.push(t1);
			changeTable()
        }
    });
};
function changeTable() {
	console.log("ppppp");
	
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
						columns: questionColumns,
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
												if (key != 0 && key != "路径") {
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
							return {
								"total" : data.total,// 总页数
								"rows" : data.rows, // 数据
								"files" : data.files,// 文件列表
								"attribute" : data.attribute
							};
						},
						
						queryParams : function queryParams(params) { // 设置查询参数
							var param = {
								pageNumber : params.offset + 1,
								pageSize : params.limit,
								//keys : keys.join("."),
								//values : values.join("."),
								flag:"b"

							};
							console.log("pageNumber" + param.pageNumber);
							console.log("pageSize" + param.pageSize);
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
