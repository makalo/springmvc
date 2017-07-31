package com.huawei.tools.dao;


import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.Attribute; 
import com.huawei.tools.change.Repair;
public class ReadFile {
	public static boolean tag=true;
	public static Logger loggerRF = Logger.getLogger(ReadFile.class);
	Repair repair=null;
	public ReadFile()
	{
		repair=new Repair();
	}
    /**
     * 读取xlsx
     * @param file
     * @return
     * @throws IOException
     */
    public List<List<List<String>>>  readExcel2007(File file)  {  
    	List<List<List<String>>> list_sheet=new ArrayList<List<List<String>>>();
		try {
			InputStream is = new FileInputStream(file);
			XSSFWorkbook xwb = new XSSFWorkbook(is);
			for (int numSheet = 0; numSheet < xwb.getNumberOfSheets(); numSheet++) {  
	            XSSFSheet xSheet = xwb.getSheetAt(numSheet);  
	             
	            if (xSheet == null) { 
	            	list_sheet.add(null);
	                continue;  
	            }
	            repair.repairRegion(xSheet, "excel2007");
	            List<List<String>> list_row = new ArrayList<List<String>>();
	           
	            boolean flag=true;
	            int attributeNum=0;
	            for (int rowNum = 0; rowNum <= xSheet.getLastRowNum(); rowNum++) {  
	                XSSFRow xRow = xSheet.getRow(rowNum);               
	                if (xRow == null) {
	                    continue;  
	                }
	                else{
	                if(flag){
	                	attributeNum=xSheet.getRow(rowNum).getLastCellNum()+1;
	                	flag=false;
	                }
	                List<String> content = new ArrayList<String>();
	                String cell; 
	                for (int cellNum = 0; cellNum < attributeNum; cellNum++) { 
	                	
	                    XSSFCell xCell = xRow.getCell(cellNum);  
	                    if (xCell == null) {  
	                    	content.add("none");  
	                    }
	                    else{
	                    xCell.setCellType(Cell.CELL_TYPE_STRING);
	                    cell = xCell.getStringCellValue();
				        content.add(cell);
	                    }
	                }
	                if(rowNum>0 && content.size()<attributeNum)
	                {
	                	for(int i=content.size();i<attributeNum;i++)
	                    {
	                    	content.add("none");
	                    }
	                }                
	                list_row.add(content);
	                }
	            }
	            list_sheet.add(list_row);          
	        }  
			repair.iOstream.saveOutStream(xwb, file.getName(), "excel2007");
	        is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerRF.error(e);
		} 
        return list_sheet ;  
    }
    /**
     * 读取xls
     * @param file
     * @return
     * @throws IOException
     */
    public List<List<List<String>>>  readExcel(File file){  
    	List<List<List<String>>> list_sheet=new ArrayList<List<List<String>>>();
		try {
			InputStream is = new FileInputStream(file);
			 HSSFWorkbook hwb = new HSSFWorkbook(is);
			 for (int numSheet = 0; numSheet < hwb.getNumberOfSheets(); numSheet++) {  
		            HSSFSheet hSheet = hwb.getSheetAt(numSheet);  
		             
		            if (hSheet == null) {  
		                continue;  
		            }  
		            repair.repairRegion(hSheet, "excel");
		            List<List<String>> list_row = new ArrayList<List<String>>();
		           
		            boolean flag=true;
		            int attributeNum=0;
		            for (int rowNum = 0; rowNum <= hSheet.getLastRowNum(); rowNum++) {  
		                HSSFRow hRow = hSheet.getRow(rowNum);               
		                if (hRow == null) {
		                    continue;  
		                }
		                else{
		                if(flag){
		                	attributeNum=hSheet.getRow(rowNum).getLastCellNum()+1;
		                	flag=false;
		                }
		                List<String> content = new ArrayList<String>();
		                String cell; 
		                for (int cellNum = 0; cellNum <attributeNum; cellNum++) { 
		                	
		                    HSSFCell hCell = hRow.getCell(cellNum);  
		                    if (hCell == null) {  
		                    	content.add("none");  
		                    }
		                    else{
		                    hCell.setCellType(Cell.CELL_TYPE_STRING);
		                    cell = hCell.getStringCellValue();
					        content.add(cell);
		                    }
		                }
		                if(rowNum>0 && content.size()<attributeNum)
		                {
		                	for(int i=content.size();i<attributeNum;i++)
		                    {
		                    	content.add("none");
		                    }
		                }                
		                list_row.add(content);
		                }
		            }
		            list_sheet.add(list_row);
		        }  
			   repair.iOstream.saveOutStream(hwb, file.getName(), "excel");
		       is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerRF.error(e);
		}
        return list_sheet ;  
    }   
    /**
     * 读取txt
     * @param file
     * @return
     * @throws IOException
     */
    public String readTxt(File file){ 
    	try {
    		StringBuffer sb = new StringBuffer("");  
            InputStream is = new FileInputStream(file);  
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));  
            try {  
                String line = "";  
                while ((line = reader.readLine()) != null) {  
                    sb.append(line + "\r");  
                }  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            }  
            is.close();
            return sb.toString().trim();  
		} catch (Exception e) {
			// TODO: handle exception
			loggerRF.error(e);
		}
		return null;
    }  
    
    /** 
     * 获取文件的xml对象，然后获取对应的根节点root 
     */  
    public List<HashMap<String,String>> readXml(File file){  
    	List<HashMap<String, String>>listData=new ArrayList<HashMap<String,String>>();
        final SAXReader sax = new SAXReader();
        Document document;
		try {
			document = sax.read(file);
			final Element root = document.getRootElement();
	        getNodes(root,listData);
		} catch (DocumentException e) {
			loggerRF.error(e);
		}// 获取document对象,如果文档无节点，则会抛出Exception提前结束  
        return listData;
    }
  
    /** 
     * 从指定节点Element node开始,递归遍历其所有子节点 
     * @return 
     */  
    @SuppressWarnings("unchecked")
	public void getNodes(Element node,List<HashMap<String, String>> listData) {  
        // 递归遍历当前节点所有的子节点  
        List<Element> listElement =node.elements();// 所有一级子节点的list
        HashMap<String, String> dataMap=new HashMap<String,String>();
        for (int i=0;i<listElement.size();i++) {// 遍历所有一级子节点  
        	
        	Element e=listElement.get(i);
        	
        	if(e.elements().size()==0)
        	{
        		if(!(e.getTextTrim().equals("")))
        		{
        			dataMap.put(e.getName(), e.getTextTrim());
        		}
        		else{
        			dataMap.put(e.getName(),"none");
        		}
        		
        		tag=false;
        		if(i==listElement.size()-1)
        		{
        			List<Attribute> listAttr = e.getParent().attributes();
        			if (listAttr.size()!=0)
        			{
        				StringBuilder attr=new StringBuilder();
        				for(Attribute attribute : listAttr){  
        		           attr.append(attribute.getName() +":" + attribute.getValue()+"  ");
        		        }
        				dataMap.put("节点属性", attr.toString());
        			}else{
        				dataMap.put("节点属性", "none");
        			}
        			dataMap.put("路径",e.getParent().getUniquePath());
        			listData.add(dataMap);
        			tag=true;
        		}
        	}
        	else{
        		if(!tag)
        		{
        			List<Attribute> listAttr = e.getParent().attributes();
        			if (listAttr.size()!=0)
        			{
        				StringBuilder attr=new StringBuilder();
        				for(Attribute attribute : listAttr){  
        		           attr.append(attribute.getName() +":" + attribute.getValue()+"  ");
        		        }
        				dataMap.put("节点属性", attr.toString());
        			}else{
        				dataMap.put("节点属性", "none");
        			}
        			dataMap.put("路径",e.getParent().getUniquePath());
        			listData.add(dataMap);//特别注意，深入和回调时的函数对象不一样
        			tag=true;
        		}
        		getNodes(e,listData);// 递归  
        	}
        	
        }  
    }   
}  
