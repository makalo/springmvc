$(function () {
    //0.初始化fileinput
    var oFileInput = new FileInput();
    oFileInput.Init("filesUpload", "upload");
});
//初始化fileinput
var FileInput = function () {
    var oFile = new Object();

    //初始化fileinput控件（第一次初始化）
    oFile.Init = function(ctrlName, uploadUrl) {
    var control = $('#' + ctrlName);

    //初始化上传控件的样式
    control.fileinput({
        language: 'zh', //设置语言
        uploadUrl: uploadUrl, //上传的地址
        uploadAsync: true,
        allowedFileExtensions: ['xlsx','xls','xml','txt'],//接收的文件后缀
        preferIconicPreview: true,
        previewFileIcon: '<i class="fa fa-file"></i>',
        allowedPreviewTypes: null, // allow only preview of image & text files
        previewFileIconSettings: {
            'xls': '<i class="fa fa-file-excel-o text-success"></i>',            
        },
        previewFileExtSettings: {
        	'xls': function(ext){      
                   return ext.match(/(xls|xlsx|xml)$/i);
            },
        },
        fileActionSettings: {
            showZoom: false,           
        },
        showUpload: true, //是否显示上传按钮
        showCaption: false,//是否显示标题
        browseClass: "btn btn-primary", //按钮样式     
        maxFileCount: 8, //表示允许同时上传的最大文件个数
        enctype: 'multipart/form-data',
        validateInitialCount:true,
       
        msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
    });
    
     //导入文件上传完成之后的事件
    $("#filesUpload").on("fileuploaded", function (event, data, previewId, index) {
    	
       /* $("#uploadModal").modal("hide");*/
    
    	console.log(data.response.msg);
     
    });
}
    return oFile;
};

//当modal关闭时，对元素内容清空
$('#uploadModal').on('hide.bs.modal', function () {
	$('#filesUpload').fileinput('clear');

})
