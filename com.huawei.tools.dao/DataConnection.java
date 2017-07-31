package com.huawei.tools.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DataConnection {
	public static Logger loggerDC= Logger.getLogger(DataConnection.class);
	public DataConnection()
	{
		loggerDC.info("正在遍历文件");
	}
	
	/**
     * 过滤目录下的文件
     * @param path 存放表格的文件的目录
     * @return 返回文件list
     */
	List<File> fileList = new ArrayList<File>();
    public List<File> getFileList(String path) {
    	String dirPath = path;
    	File folder=new File(dirPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFileList(files[i].getAbsolutePath());
                } else if (isAllowFile(files[i].getName())) {
                	//File file = new File(folder, files[i]);
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return fileList;
    }
    
    /**
     * 判断是否为目标文件，目前支持xlsx xls xml格式
     * @param fileName 文件名称
     * @return 如果是文件类型满足过滤条件，返回true；否则返回false
     */
    public boolean isAllowFile(String fileName) {
        if (fileName.lastIndexOf(".xlsx") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".xls") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".xml") > 0) {
            return true;
        }else if(fileName.lastIndexOf(".txt") > 0)
        	return true;
        else
        	return false;
    }
}
