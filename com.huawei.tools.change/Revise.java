package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Revise {
	public static Logger loggerRE= Logger.getLogger(Revise.class);
	IOstream io=null;
	Repair repair=null;
	public Revise(IOstream iOstream,Repair repair)
	{
		this.repair=repair;
		this.io=iOstream;
	}
	/**
	 * excel修改
	 * 
	 * @throws IOException
	 */
	public void reviseExcel2007(int sheetNum, int rowNum, String fileName,HashMap<String, String> changeContent) {
		loggerRE.info("正在修改excel"+fileName);
		FileInputStream FileInputStream =io.loadFile(fileName);
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(FileInputStream);
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);
			XSSFRow row = null;
			int rowCount = -1;
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				if (sheet.getRow(i) != null) {
					rowCount++;
				}
				if (rowCount == rowNum) {
					row = sheet.getRow(i);
					break;
				}
			}
			Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
					XSSFCell cell_attr = sheet.getRow(0).getCell(cellNum);// 不健壮，得重新设计一下
					XSSFCell cell = row.getCell(cellNum);
					if (entry.getKey().equals(cell_attr.getStringCellValue())) {
						String content = entry.getValue();
						cell.setCellValue(content);
					}
				}
			}
			io.closeInputStream(FileInputStream);// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
			io.saveOutStream(workbook, fileName, "excel2007");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerRE.error(e);
		}
	}
	/**
	 * excel(XLS)文件修改
	 * 
	 * @throws IOException
	 */
	public void reviseExcel(int sheetNum, int rowNum, String fileName,HashMap<String, String> changeContent) {
		loggerRE.info("正在修改excel"+fileName);
		FileInputStream FileInputStream = io.loadFile(fileName);
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(FileInputStream);
			HSSFSheet sheet = workbook.getSheetAt(sheetNum);
			HSSFRow row = null;
			int rowCount = -1;
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				if (sheet.getRow(i) != null) {
					rowCount++;
				}
				if (rowCount == rowNum) {
					row = sheet.getRow(i);
					break;
				}
			}
			Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
					HSSFCell cell_attr = sheet.getRow(0).getCell(cellNum);// 不健壮，得重新设计一下
					HSSFCell cell = row.getCell(cellNum);
					if (entry.getKey().equals(cell_attr.getStringCellValue())) {
						String content = entry.getValue();
						cell.setCellValue(content);
					}
				}
			}
			io.closeInputStream(FileInputStream);// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
			io.saveOutStream(workbook, fileName, "excel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerRE.error(e);
		}
	}
	/**
	 * 修改XML的值
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void reviseXML(String nodePath, String fileName,HashMap<String, String> changeContent) {
		loggerRE.info("正在修改XML"+fileName);
		SAXReader saxReader = new SAXReader();
		FileInputStream FileInputStream =io.loadFile(fileName);
		Document document;
		try {
			document = saxReader.read(FileInputStream);
			Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				if (!entry.getKey().equals("路径")) {
					Element element = (Element) document.selectSingleNode(nodePath + "/" + entry.getKey());
					if (element == null) {
						System.err.println("不存在该节点");
					} else {
						element.setText(entry.getValue());
					}
				}
			}
			io.saveOutStream(document, fileName, "xml");
			io.closeInputStream(FileInputStream);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			loggerRE.error(e);
		}
	}

}
