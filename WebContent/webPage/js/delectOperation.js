var $remove = $('#remove');
var $table=$('#resultsTable');
//绑定操作单条删除执行的事件
var operateEvents={'click .remove': function (e, value, row, index) {
           $table.bootstrapTable('remove', {
               field: 'id',
               values: [row.id]
           });
           var delLoctions=new Array();
           delLoctions.push(row.路径);
           deleteDatas(delLoctions);          
       }
}
//表格中每一行删除操作的格式
function operateFormatter(){
    var removeItem='<a class="remove" href="javascript:void(0)" title="Remove">'+'删除'+'</a>'
    return removeItem;
}
//批量删除操作
function deleteItems(){	
    var ids = getSelections("id");
    var delLoctions=getSelections("location");
    $table.bootstrapTable('remove', {
         field: 'id',
         values: ids
     });
    deleteDatas(delLoctions)
      //  $remove.prop('disabled', true);
    }
//获取批量删除的id值和位置值，id用于前端页面删除，路径用于后台数据删除
function getSelections(key) {
	if(key=="id"){	
		return $.map($table.bootstrapTable('getSelections'), function (row) {    	
	        return row.id
	    });
	}else if(key=="location"){
		return $.map($table.bootstrapTable('getSelections'), function (row) {    	
	        return row.路径
	    });
	}
}
//删除后台表格数据
function deleteDatas(delLoctions){
	console.log(delLoctions);
	if(delLoctions!=null){
	var deletes=new Array();
	for (var i=0;i<delLoctions.length;i++){
		var deleteItem=new Object();
		deleteItem.location=delLoctions[i];
		deleteItem.allvalues="";
		deletes[i]=deleteItem;
	}
	var json={"deleteItems": JSON.stringify(deletes)};
	//alert(JSON.stringify(deleteItems));
	  $.ajax({
	        type:'POST',
	        data:json,
	        contentType :'application/x-www-form-urlencoded;charset=UTF-8',
	        dataType:'json',
	        url :'deleteFileItems',
	        success: function(data) {
	        	console.log("删除数据成功");        	    
	        },
	        error: function(XMLHttpRequest, textStatus, errorThrown) {
	            console.log("删除数据出现错误");
	            console.log("HttpStatus:"+XMLHttpRequest.status+" "+"readyState:"+XMLHttpRequest.readyState+" "+"textStatus:"+textStatus);
	        },
	            
	})
	}
	
	
}
