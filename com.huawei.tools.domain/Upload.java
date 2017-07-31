package com.huawei.tools.domain;


import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;


public class Upload extends HttpServlet {
	public static Logger loggerUP = Logger.getLogger(Upload.class);
    private static final long serialVersionUID = 1L;
    private String uploadPath = "D:\\luceneData"; // 上传文件的目录
    private String tempPath = "D:\\luceneData\\buffer\\"; // 临时文件目录
    File tempPathFile; 
 
    public Upload(){
        super();
    }
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
    	init();
       try {
           DiskFileItemFactory factory = new DiskFileItemFactory();
           factory.setSizeThreshold(4096); // 设置缓冲区大小，这里是4kb
           factory.setRepository(tempPathFile);// 设置缓冲区目录
           ServletFileUpload upload = new ServletFileUpload(factory);
           upload.setSizeMax(4194304); // 设置最大文件尺寸，这里是4MB
           List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
           Iterator<FileItem> i = items.iterator();
           while (i.hasNext()) {
              FileItem fi = (FileItem) i.next();
              String fileName = fi.getName();
              if (fileName != null) {
                  File fullFile = new File(fi.getName());
                  File savedFile = new File(uploadPath, fullFile.getName());
                  fi.write(savedFile);
                  
              }
           }
       } catch (Exception e) {
    	   loggerUP.error(e);
       }
    }
    public void init(){
    	try {
    		File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) {
                uploadFile.mkdirs();
            }
            File tempPathFile = new File(tempPath);
             if (!tempPathFile.exists()) {
                tempPathFile.mkdirs();
            }
		} catch (Exception e) {
			 loggerUP.error(e);
		}
        
     }
}
