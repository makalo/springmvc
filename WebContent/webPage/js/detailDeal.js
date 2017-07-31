var modifyAllValues=new Array();

function modifyObject(){
	this.location="";
	this.allValues="";
}
function enbleEdit (editBotton,arrayIndex){
	var modifyValues=new modifyObject();
	
	var $editBotton=$(editBotton);
	$editBotton.attr('disabled',"true");	
	$editBotton.nextAll(".msgSpan").css("display","inline-block");
	$editBotton.nextAll(".msgSpan").text("单击对应属性修改对应值呦！");
	
    var location=$editBotton.parents(".detailPage").find(".location span").text();
    modifyValues.location=location;
    var itemValue=new Array();
    $('.enEditSpan').unbind("click"); //移除上一次click绑定
    $(".enEditSpan").click(function(){
       var span = $(this).find("span.valueSpan");
       var txt = span.text();
       var input = $("<input type='text' value='"+txt+"'/>");
       span.html(input);
       input.click(function() { return false; }); 
    //获取焦点
      input.trigger("focus");
    //文本框失去焦点后提交内容，重新变为文本
      
      input.blur(function() {
           var newtxt = $(this).val();
           if(newtxt.trim()!=txt.trim()){
        	   
               var key=$(this).parents("p").find(".keySpan b").text();
               var key=key.substring(0,key.lastIndexOf(":"));
               
               var item={key:key,value:newtxt};  
               itemValue.push(item); 
               modifyValues.allValues=JSON.stringify(itemValue);
               console.log("index"+arrayIndex)
               modifyAllValues[arrayIndex]=modifyValues;        
         }
         span.html(newtxt);
      }); 
   });
}
function enbleSave(saveBotton,arrayIndex){
    //暂定用字符串传输，后期需要尝试用json数据传输
	$('.enEditSpan').unbind("click"); //移除click
	var $saveBotton=$(saveBotton);
	$saveBotton.prev("button").removeAttr("disabled");
	var allitems=new Array();
	allitems.push(modifyAllValues[arrayIndex]);
	var json = {"saveItems": JSON.stringify(allitems)};
 
    $.ajax({
        type:'POST',
        data:json,
        contentType :'application/x-www-form-urlencoded;charset=UTF-8',
        dataType:'json',
        url :'editFile',
        success: function(data) {
        	console.log("修改数据成功");        	
        	$saveBotton=$(saveBotton);
        	$saveBotton.nextAll(".msgSpan").css("display","inline-block");
        	$saveBotton.nextAll(".msgSpan").text(data.msg);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log("修改数据传输出现错误");
            console.log("HttpStatus:"+XMLHttpRequest.status+" "+"readyState:"+XMLHttpRequest.readyState+" "+"textStatus:"+textStatus);
        },
            
})

     
}
