package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class AddAttr {
	public static Logger loggerAA= Logger.getLogger(AddAttr.class);
	IOstream io=null;
	Repair repair=null;
	public AddAttr(IOstream iOstream,Repair repair)
	{
		this.repair=repair;
		this.io=iOstream;
	}
	/**
	 * excel增加属性
	 */
	public void addExcelAttribute2007() {

	}
	/**
	 * excel(XLS)增加属性
	 */
	public void addExcelAttribute() {

	}
	/**
	 * 为XML添加属性
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void addXMLAttribute(String nodePath, String fileName,HashMap<String, String> changeContent) {
		loggerAA.info("正在添加XML属性"+fileName);
		SAXReader saxReader = new SAXReader();
		FileInputStream FileInputStream= io.loadFile(fileName);
		Document document;
		try {
			document = saxReader.read(FileInputStream);
			Element element = (Element) document.selectSingleNode(nodePath);
			Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				Element item = DocumentHelper.createElement(entry.getKey());
				item.setText(entry.getValue());
				element.add(item);
			}
			io.saveOutStream(document, fileName, "xml");
			io.closeInputStream(FileInputStream);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			loggerAA.error(e);
		}
	}
}
