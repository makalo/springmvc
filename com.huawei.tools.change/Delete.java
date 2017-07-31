package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Delete {
	public static Logger loggerDEL= Logger.getLogger(Delete.class);
	IOstream io=null;
	Repair repair=null;
	public Delete (IOstream iOstream,Repair repair)
	{
		this.io=iOstream;
		this.repair=repair;
	}
	/**
	 * excel删除一行
	 * 
	 * @throws IOException
	 */
	public void delExcelRow2007(int sheetNum, String fileName, List<Integer> listRow) {
		loggerDEL.info("正在删除excel行"+fileName);
		FileInputStream FileInputStream = io.loadFile(fileName);
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(FileInputStream);
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);
			for (Integer n : listRow) {
				XSSFRow row = sheet.getRow(n);
				if(row!=null)
				{
					sheet.removeRow(row);
				}
				// sheet.shiftRows(n+1, sheet.getLastRowNum(), -1);
			}
			io.closeInputStream(FileInputStream);// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
			io.saveOutStream(workbook, fileName, "excel2007");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerDEL.error(e);
		}

	}
	/**
	 * excel(XLS)删除一行
	 * 
	 * @throws IOException
	 */
	public void delExcelRow(int sheetNum, String fileName, List<Integer> listRow) {
		loggerDEL.info("正在删除excel行"+fileName);
		FileInputStream FileInputStream = io.loadFile(fileName);
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(FileInputStream);
			HSSFSheet sheet = workbook.getSheetAt(sheetNum);
			for (Integer n : listRow) {
				HSSFRow row = sheet.getRow(n);
				sheet.removeRow(row);
				// sheet.shiftRows(n+1, sheet.getLastRowNum(), -1);
			}
			io.closeInputStream(FileInputStream);// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
			io.saveOutStream(workbook, fileName, "excel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerDEL.error(e);
		}
	}
	/**
	 * 删除XML一行
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void delXMLRow(String nodePath, String fileName) {
		loggerDEL.info("正在删除XML行"+fileName);
		SAXReader saxReader = new SAXReader();
		FileInputStream FileInputStream = io.loadFile(fileName);
		Document document;
		try {
			document = saxReader.read(FileInputStream);
			Element element = (Element) document.selectSingleNode(nodePath);
			if (element != null) {
				element.detach();
			}
			io.saveOutStream(document, fileName, "xml");
			io.closeInputStream(FileInputStream);
		} catch (DocumentException e) {
			loggerDEL.error(e);
		}
	}
}
