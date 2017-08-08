$(document).ready(function(){
	//获取所有属性值
	$.ajax({
		type : "GET",
		url : "getAllAttributes",
		async: false,  
		dataType : "json",
		success : function(data) {
			attribute = data;
			$.each(attribute, function(i, item) {
				if(item!="路径"||item!="子路径"){
					$(".SearchOptions").append(
							 '<option>'+item+'</option>');
					}
				
			});
		}
	});
	
})
