package com.huawei.tools.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Change {
	// 目前的问题
	// 不能忽略.的特殊性
	// 对索引微改（需要优化的地方，每次修改一个地方都对整个数据进行重建索引？
	// xml建立修改
	//
	// 如何不重复将文件读入类存（重复利用内存中的缓存，在changeExcel2007函数里面，如何不关闭输入流，反复利用上次读取的输入流）
	// xml有两个问题：1.删除大类时，小类也被删除2.添加一行时属性可以不按标准
	// Excel有一个问题：删除一行时留下空格
	private HashMap<String, String> changeContent = null;
	static FileInputStream FileInputStream = null;
	int needChangeNum = 0;
	static int counter = 0;
	String local = null;

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
			System.out.println("进行删除");
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
	public String fileName(String[] location) {
		String fileName = location[0];
		return fileName;
	}

	/**
	 * 判断需要改的是什么类型的文件
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void changeControler(String opType, List<Integer> listRow) throws IOException, DocumentException {
		String[] location = getChangeLocation();
		String fileName = fileName(location);
		String[] typeSplit = fileName.split("\\.");
		String type = typeSplit[typeSplit.length - 1];
		if ("xlsx".equalsIgnoreCase(type)) {
			int xsheetNum = Integer.parseInt(location[1]);
			int xrowNum = Integer.parseInt(location[2]);
			//int attrNum = changeContent.size();
			if ("revise".equalsIgnoreCase(opType)) {
				reviseExcel2007(xsheetNum, xrowNum, fileName);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				if (isAllowDel(xrowNum, listRow)) {
					delExcelRow2007(xsheetNum, fileName, listRow);
					repairExcel2007(xsheetNum, fileName);
				}
			} else if ("addRow".equalsIgnoreCase(opType)) {
				addExcelRow2007(xsheetNum, xrowNum + 1, 9, fileName);// 属性数量问题
				repairExcel2007(xsheetNum, fileName);
			}
		} else if ("xls".equalsIgnoreCase(type)) {
			int hsheetNum = Integer.parseInt(location[1]);
			int hrowNum = Integer.parseInt(location[2]);
			//int attrNum = changeContent.size();
			if ("revise".equalsIgnoreCase(opType)) {
				reviseExcel(hsheetNum, hrowNum, fileName);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				if (isAllowDel(hrowNum, listRow)) {
					delExcelRow(hsheetNum, fileName, listRow);
					repairExcel(hsheetNum, fileName);
				}
			} else if ("addRow".equalsIgnoreCase(opType)) {
				addExcelRow(hsheetNum, hrowNum + 1, 9, fileName);// 属性数量问题
				repairExcel(hsheetNum, fileName);
			}
		} else if ("xml".equalsIgnoreCase(type)) {
			String nodePath = location[1];
			if ("revise".equalsIgnoreCase(opType)) {
				reviseXML(nodePath, fileName);
			} else if ("delRow".equalsIgnoreCase(opType)) {
				delXMLRow(nodePath, fileName);
			} else if ("addRow".equalsIgnoreCase(opType)) {
				addXMLRow(nodePath, fileName);
			}
		} else if ("txt".equalsIgnoreCase(type)) {

		}
	}

	/**
	 * excel修改
	 * 
	 * @throws IOException
	 */
	public void reviseExcel2007(int sheetNum, int rowNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
		XSSFWorkbook workbook = new XSSFWorkbook(FileInputStream);
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
		closeInputStream();// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
		saveOutStream(workbook, fileName, "excel2007");
	}

	/**
	 * excel删除一行
	 * 
	 * @throws IOException
	 */
	public void delExcelRow2007(int sheetNum, String fileName, List<Integer> listRow) throws IOException {
		FileInputStream = loadFile(fileName);
		XSSFWorkbook workbook = new XSSFWorkbook(FileInputStream);
		XSSFSheet sheet = workbook.getSheetAt(sheetNum);
		for (Integer n : listRow) {
			XSSFRow row = sheet.getRow(n);
			sheet.removeRow(row);
			// sheet.shiftRows(n+1, sheet.getLastRowNum(), -1);
		}
		closeInputStream();// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
		saveOutStream(workbook, fileName, "excel2007");
	}

	/**
	 * excel(插入)增加一行
	 * 
	 * @throws IOException
	 */
	public void addExcelRow2007(int sheetNum, int startRow, int attrNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
		XSSFWorkbook workbook = new XSSFWorkbook(FileInputStream);
		XSSFCellStyle cellStyle = setCellFont2007(workbook);
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
		closeInputStream();
		saveOutStream(workbook, fileName, "excel2007");
	}

	/**
	 * excel修复表格
	 * 
	 * @throws IOException
	 */
	public void repairExcel2007(int sheetNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
		XSSFWorkbook workbook = new XSSFWorkbook(FileInputStream);
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		XSSFSheet sheet = workbook.getSheetAt(sheetNum);
		for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
			XSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				// sheet.shiftRows(rowNum+1,sheet.getLastRowNum(),-1);
				System.out.println("有空格");//此处还有问题
			} else {
				row.setHeight((short) 500);
			}
		}
		closeInputStream();
		saveOutStream(workbook, fileName, "excel2007");
	}

	/**
	 * excel增加属性
	 */
	public void addExcelAttribute2007() {

	}

	public XSSFCellStyle setCellFont2007(XSSFWorkbook workbook) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("微软雅黑");
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * excel(XLS)文件修改
	 * 
	 * @throws IOException
	 */
	public void reviseExcel(int sheetNum, int rowNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
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
		closeInputStream();// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
		saveOutStream(workbook, fileName, "excel");
	}

	/**
	 * excel(XLS)删除一行
	 * 
	 * @throws IOException
	 */
	public void delExcelRow(int sheetNum, String fileName, List<Integer> listRow) throws IOException {
		FileInputStream = loadFile(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook(FileInputStream);
		HSSFSheet sheet = workbook.getSheetAt(sheetNum);
		for (Integer n : listRow) {
			HSSFRow row = sheet.getRow(n);
			sheet.removeRow(row);
			// sheet.shiftRows(n+1, sheet.getLastRowNum(), -1);
		}
		closeInputStream();// 问题所在，每次修改都要触发这个函数，如何才能读取之后就不在读取，而是反复利用已经读取了的，其他地方也一样
		saveOutStream(workbook, fileName, "excel");
	}

	/**
	 * excel(XLS)(插入)增加一行
	 * 
	 * @throws IOException
	 */
	public void addExcelRow(int sheetNum, int startRow, int attrNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook(FileInputStream);
		HSSFCellStyle cellStyle = setCellFont(workbook);
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
		closeInputStream();
		saveOutStream(workbook, fileName, "excel");
	}

	/**
	 * excel(XLS)修复表格
	 * 
	 * @throws IOException
	 */
	public void repairExcel(int sheetNum, String fileName) throws IOException {
		FileInputStream = loadFile(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook(FileInputStream);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		HSSFSheet sheet = workbook.getSheetAt(sheetNum);
		for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
			HSSFRow row = sheet.getRow(rowNum);
			if (row == null) {
				// sheet.shiftRows(rowNum+1,sheet.getLastRowNum(),-1);
				System.out.println("有空格");
			} else {
				row.setHeight((short) 500);
			}
		}
		closeInputStream();
		saveOutStream(workbook, fileName, "excel");
	}

	/**
	 * excel(XLS)增加属性
	 */
	public void addExcelAttribute() {

	}
	/**
	 * excel(XLS)设置格式和字体
	 * 
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle setCellFont(HSSFWorkbook workbook) {
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("微软雅黑");
		cellStyle.setFont(font);
		return cellStyle;
	}
    /**
     * 判断是否可以删除
     */
	public boolean isAllowDel(int rowNum, List<Integer> listRow) {
		counter++;
		listRow.add(rowNum);
		if (counter == needChangeNum) {
			Collections.sort(listRow, Collections.reverseOrder());
			// Collections.sort(listRow);
			return true;
		}
		return false;
	}
	/**
	 * 修改XML的值
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void reviseXML(String nodePath, String fileName) throws DocumentException, IOException {
		SAXReader saxReader = new SAXReader();
		FileInputStream = loadFile(fileName);
		Document document = saxReader.read(FileInputStream);
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
		saveOutStream(document, fileName, "xml");
		closeInputStream();
	}

	/**
	 * 增加XML一行
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void addXMLRow(String nodePath, String fileName) throws DocumentException, IOException {
		SAXReader saxReader = new SAXReader();
		FileInputStream = loadFile(fileName);
		Document document = saxReader.read(FileInputStream);
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
		saveOutStream(document, fileName, "xml");
		closeInputStream();
	}

	/**
	 * 删除XML一行
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void delXMLRow(String nodePath, String fileName) throws IOException, DocumentException {
		SAXReader saxReader = new SAXReader();
		FileInputStream = loadFile(fileName);
		Document document = saxReader.read(FileInputStream);
		Element element = (Element) document.selectSingleNode(nodePath);
		if (element != null) {
			element.detach();
		}
		saveOutStream(document, fileName, "xml");
		closeInputStream();
	}

	/**
	 * 为XML添加属性
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void addXMLAttribute(String nodePath, String fileName) throws IOException, DocumentException {
		SAXReader saxReader = new SAXReader();
		FileInputStream = loadFile(fileName);
		Document document = saxReader.read(FileInputStream);
		Element element = (Element) document.selectSingleNode(nodePath);
		Iterator<Entry<String, String>> iterator = changeContent.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			Element item = DocumentHelper.createElement(entry.getKey());
			item.setText(entry.getValue());
			element.add(item);
		}
		saveOutStream(document, fileName, "xml");
		closeInputStream();
	}

	/**
	 * 产生输入流
	 * 
	 * @throws FileNotFoundException
	 */
	public FileInputStream loadFile(String path) throws FileNotFoundException {
		FileInputStream = new FileInputStream("D:\\luceneData\\" + path);
		return FileInputStream;
	}

	/**
	 * 关闭输入文件流
	 * 
	 * @throws IOException
	 */
	public void closeInputStream() throws IOException {
		FileInputStream.close();
	}

	/**
	 * 保存输出流
	 * 
	 * @throws IOException
	 */
	public void saveOutStream(Object op, String fileName, String tpye) throws IOException {
		if ("excel2007".equalsIgnoreCase(tpye)) {
			XSSFWorkbook workbook = (XSSFWorkbook) op;
			FileOutputStream excelFileOutPutStream = new FileOutputStream("D:\\luceneData\\" + fileName);
			workbook.write(excelFileOutPutStream);
			excelFileOutPutStream.flush();
			excelFileOutPutStream.close();
		} else if ("excel".equalsIgnoreCase(tpye)) {
			HSSFWorkbook workbook = (HSSFWorkbook) op;
			FileOutputStream excelFileOutPutStream = new FileOutputStream("D:\\luceneData\\" + fileName);
			workbook.write(excelFileOutPutStream);
			excelFileOutPutStream.flush();
			excelFileOutPutStream.close();
		} else if ("xml".equalsIgnoreCase(tpye)) {
			Document document = (Document) op;
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(new FileOutputStream("D:\\luceneData\\" + fileName), format);
			writer.write(document);
			writer.flush();
			writer.close();
		}
	}
}
