package com.huawei.tools.change;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AdjustmentCenter {
	// 目前的问题
	// 不能忽略.的特殊性
	// 对索引微改（需要优化的地方，每次修改一个地方都对整个数据进行重建索引？
	// xml建立修改
	//
	// 如何不重复将文件读入类存（重复利用内存中的缓存，在changeExcel2007函数里面，如何不关闭输入流，反复利用上次读取的输入流）
	// xml有两个问题：1.删除大类时，小类也被删除2.添加一行时属性可以不按标准
	// Excel有一个问题：删除一行时留下空格
	public static Logger loggerAC = Logger.getLogger(AdjustmentCenter.class);
	private HashMap<String, String> changeContent = null;
	IOstream iOstream=null;
	Repair repair=null;
	Delete delete=null;
	Add add=null;
	Revise revise=null;
	AddAttr addAttr=null;
	int needChangeNum = 0;
	static int counter = 0;
	String local = null;


	public AdjustmentCenter()
	{
		iOstream=new IOstream();
		repair=new Repair();
		revise=new Revise(iOstream, repair);
		add=new Add(iOstream, repair);
		delete=new Delete(iOstream, repair);
		addAttr=new AddAttr(iOstream, repair);
	}
	/**
	 * 映射成map
	 * 
	 * @param keys
	 * @param values
	 */
	public void init(JSONObject jsonObject, int changeNum) {
		changeContent = new HashMap<String, String>();
		needChangeNum = changeNum;
		local = (String) jsonObject.get("location");
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.get("allValues"));// 关键字有问题
		System.out.println(jsonArray);
		if (jsonArray.toString().equalsIgnoreCase("[null]")) {
			loggerAC.info("json数组为空");
		} else {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonattr = (JSONObject) jsonArray.get(i);
				changeContent.put((String) jsonattr.get("key"), (String) jsonattr.get("value"));
			}
		}
	}

	/**
	 * 找到相应文件的location
	 */
	public String[] getChangeLocation() {
		String[] location = local.split(":");
		return location;
	}

	/**
	 * 找到文件名
	 */
	public String getFileName(String[] location) {
		String fileName = location[0];
		return fileName;
	}

	/**
	 * 判断需要改的是什么类型的文件
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void changeControler(String opType, List<Integer> listRow) {
		String[] location = getChangeLocation();
		String fileName = getFileName(location);
		String[] typeSplit = fileName.split("\\.");
		String type = typeSplit[typeSplit.length - 1];
		if ("xlsx".equalsIgnoreCase(type)) {
			int xsheetNum = Integer.parseInt(location[1]);
			int xrowNum = Integer.parseInt(location[2]);
			// int attrNum = changeContent.size();
			if ("revise".equalsIgnoreCase(opType)) {
				revise.reviseExcel2007(xsheetNum, xrowNum, fileName,changeContent);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				if (isAllowDel(xrowNum, listRow)) {
					delete.delExcelRow2007(xsheetNum, fileName, listRow);
				}
			} else if ("addRow".equalsIgnoreCase(opType)) {
				add.addExcelRow2007(xsheetNum, xrowNum + 1, 9, fileName,changeContent);
			}
		} else if ("xls".equalsIgnoreCase(type)) {
			int hsheetNum = Integer.parseInt(location[1]);
			int hrowNum = Integer.parseInt(location[2]);
			// int attrNum = changeContent.size();
			if ("revise".equalsIgnoreCase(opType)) {
				revise.reviseExcel2007(hsheetNum, hrowNum, fileName,changeContent);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				if (isAllowDel(hrowNum, listRow)) {
					delete.delExcelRow2007(hsheetNum, fileName, listRow);
				}
			} else if ("addRow".equalsIgnoreCase(opType)) {
				add.addExcelRow2007(hsheetNum, hrowNum + 1, 9, fileName,changeContent);
			}
		} else if ("xml".equalsIgnoreCase(type)) {
			String nodePath = location[1];
			if ("revise".equalsIgnoreCase(opType)) {
				revise.reviseXML(nodePath, fileName,changeContent);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				delete.delXMLRow(nodePath, fileName);
			} else if ("addRow".equalsIgnoreCase(opType)) {
				add.addXMLRow(nodePath, fileName,changeContent);
			}
		} else if ("txt".equalsIgnoreCase(type)) {

		}
	}
	
	/**
	 * 判断是否可以删除
	 */
	public boolean isAllowDel(int rowNum, List<Integer> listRow) {
		counter++;
		listRow.add(rowNum);
		if (counter == needChangeNum) {
			Collections.sort(listRow, Collections.reverseOrder());
			counter=0;
			// Collections.sort(listRow);
			return true;
		}
		return false;
	}

}
