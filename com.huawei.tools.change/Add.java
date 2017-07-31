
package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Add {
	public static Logger loggerADD= Logger.getLogger(Add.class);
	IOstream io=null;
	Repair repair=null;
	public Add(IOstream iOstream,Repair repair)
	{
		this.io=iOstream;
		this.repair=repair;
	}
	/**
	 * excel(插入)增加一行
	 * 
	 * @throws IOException
	 */
	public void addExcelRow2007(int sheetNum, int startRow, int attrNum, String fileName,HashMap<String, String> changeContent) {
		loggerADD.info("正在添加excel行"+fileName);
		FileInputStream FileInputStream= io.loadFile(fileName);
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(FileInputStream);
			XSSFCellStyle cellStyle = repair.setCellFont2007(workbook);
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);
			int endRow = sheet.getLastRowNum();// 可能有点问题
			sheet.shiftRows(startRow, endRow, 1);
			XSSFRow newRow = sheet.createRow(startRow);
			XSSFRow attrRow = sheet.getRow(0);
			for (int i = 0; i < attrNum; i++) {
				XSSFCell newCell = newRow.createCell(i, Cell.CELL_TYPE_STRING);
				String content = changeContent.get(attrRow.getCell(i).getStringCellValue());// 需要对KEY进行对比一下再增加
				if (content != null) {
					newCell.setCellValue(content);
					newCell.setCellStyle(cellStyle);
				}
			}
			io.closeInputStream(FileInputStream);
			io.saveOutStream(workbook, fileName, "excel2007");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerADD.error(e);
		}
	}
	/**
	 * excel(XLS)(插入)增加一行
	 * 
	 * @throws IOException
	 */
	public void addExcelRow(int sheetNum, int startRow, int attrNum, String fileName,HashMap<String, String> changeContent) {
		loggerADD.info("正在添加excel行"+fileName);
		FileInputStream FileInputStream = io.loadFile(fileName);
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(FileInputStream);
			HSSFCellStyle cellStyle = repair.setCellFont(workbook);
			HSSFSheet sheet = workbook.getSheetAt(sheetNum);
			int endRow = sheet.getLastRowNum();// 可能有点问题
			sheet.shiftRows(startRow, endRow, 1);
			HSSFRow newRow = sheet.createRow(startRow);
			HSSFRow attrRow = sheet.getRow(0);
			for (int i = 0; i < attrNum; i++) {
				HSSFCell newCell = newRow.createCell(i, Cell.CELL_TYPE_STRING);
				String content = changeContent.get(attrRow.getCell(i).getStringCellValue());// 需要对KEY进行对比一下再增加
				if (content != null) {
					newCell.setCellValue(content);
					newCell.setCellStyle(cellStyle);
				}
			}
			io.closeInputStream(FileInputStream);
			io.saveOutStream(workbook, fileName, "excel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerADD.error(e);
		}
	}
	/**
	 * 增加XML一行
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void addXMLRow(String nodePath, String fileName,HashMap<String, String> changeContent) {
		loggerADD.info("正在添加XML行"+fileName);
		SAXReader saxReader = new SAXReader();
		FileInputStream FileInputStream= io.loadFile(fileName);
		Document document;
		try {
			document = saxReader.read(FileInputStream);
			Element parElement = (Element) document.selectSingleNode(nodePath);
			String name = parElement.getName();
			Element fatherNode = parElement.getParent();
			Element elt = DocumentHelper.createElement(name);
			Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				Element item = DocumentHelper.createElement(entry.getKey());
				item.setText(entry.getValue());
				elt.add(item);
			}
			fatherNode.add(elt);
			io.saveOutStream(document, fileName, "xml");
			io.closeInputStream(FileInputStream);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			loggerADD.error(e);
		}
	}
}
