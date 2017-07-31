function addItem(){
	var data=decodeURIComponent($("#addItemForm").serialize(),true);
	console.log(data);
	var submitArray=data.split("&");
	var submitData=new Object();
	var submits=new Array();
	$.each(submitArray,function(index,value){
		 var key=value.split("=")[0];
	     var values=value.split("=")[1];	     
	     if(key!="路径"){
	    	 submits.push({"key":key,"value":values})
			 console.log("uu");
	     }else{
	    	 submitData.location=currentFile;
	    	// console.log(values);
	     }	     
	});
	submitData.allValues=JSON.stringify(submits);
	var submitItems=new Array();
	submitItems.push(submitData);	
	var json={"submitItem":JSON.stringify(submitItems)}
	 $.ajax({
	        type:'POST',
	        data:json,
	        contentType :'application/x-www-form-urlencoded;charset=UTF-8',
	        dataType:'json',
	        url :'submitItem',
	        success: function(data) {
	        	console.log("增加数据成功");        	    
	        },
	        error: function(XMLHttpRequest, textStatus, errorThrown) {
	            console.log("增加数据出现错误");
	            console.log("HttpStatus:"+XMLHttpRequest.status+" "+"readyState:"+XMLHttpRequest.readyState+" "+"textStatus:"+textStatus);
	        },
	            
	})
	
}
//以后可能要改对接模式
/*	var submitData=new Object();
	var submitStr='{"';
	$.each(submitArray,function(index,value){
		 var key=value.split("=")[0];
	     var values=value.split("=")[1];
	     if(key!="路径"){
			 if(index<submitArray.length-1){ 
					 submitStr=submitStr+key+'":"'+values+'",'	 
			 }else if(i=submitArray.length-1){
				     submitStr=submitStr+key+'":"'+values+'"'	
			 }
			 
	     }else{
	    	 submitData.location=values;
	     }
	});
	submitStr=submitStr+"}"
	submitData.addItems=submitStr;
	console.log(submitData);*/
