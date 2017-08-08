$(document).ready(function(){
	//输入搜索条件空值校验
	$("#addSearchForm .attrInput").bind("blur",function(){
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
})

//当modal关闭时，对元素内容清空

$('#searchModal').on('hide.bs.modal', function () {
	$("#mutiSelectOpts").selectpicker('refresh');
	$('#searchModal').find("input").val("");
	$("#searchTable tbody").html("");
	$("#alertMsg").text("");
		$("#alertMsg").css("display","none");
		if($("#addSearchForm").hasClass("has-error")){
			$("#addSearchForm").removeClass("has-error");
		}
})
//将增加的搜索条件加入表格中
function addSearchFunc(){
	var key=$("#mutiSelectOpts").val();
	var value=$("#addSearchForm .attrInput").val();
	
	if(key!=""&&value!=""&&value!=null){
		$("#alertMsg").css("display","none");	
		
		if(!$("#addSearchForm").hasClass("has-error")){
			$("#searchTable tbody").append('<tr ><td class="col-lg-3 col-md-3 col-xs-3 col-sm-3">'+
					key+'</td><td class="col-lg-7 col-md-7 col-xs-7 col-sm-7">'+value+'</td>'+
					'</td><td class="col-lg-2 col-md-2 col-xs-2 col-sm-2"><span class="delSearchSpan" style="color:#59B8EA" onclick="delectSearchRow(this)">删除</span></td>'+
					'</tr>');
			
		}	
	}else if(key==""){
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
	if(keys.length!=0&&values.length!=0){
		$('#searchModal').modal('hide');
	    startSearch(keys,values);
	}else{
		$("#alertMsg").text("请在表格中添加检索项");
 		$("#alertMsg").css("display","inline-block");
	}
	
};
