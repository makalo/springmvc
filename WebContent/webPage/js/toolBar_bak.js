function tableCss(){
	var toolBarBtns=  $("#tableHideDiv").find('button');
    $.each(toolBarBtns,function(i,itemBtn){
    	if(i > 1){
        	$itenBtn=$(itemBtn);
        	$itenBtn.find("i").before("&nbsp;");
        	$itenBtn.find("i").after("&nbsp;");
    	}
    	
    })
    
	
}
