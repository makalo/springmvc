package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Repair {
	public static Logger loggerR= Logger.getLogger(Repair.class);
	public IOstream iOstream=null;
	public Repair()
	{
		iOstream=new IOstream();
	}
	public void repairRegion(Object sheet, String type) {
		if ("excel".equalsIgnoreCase(type)) {
			HSSFSheet hssfSheet = (HSSFSheet) sheet;
			// 遍历sheet中的所有的合并区域
			for (int i = hssfSheet.getNumMergedRegions() - 1; i >= 0; i--) {
				
				CellRangeAddress region = (CellRangeAddress) hssfSheet.getMergedRegion(i);
				Row firstRow = hssfSheet.getRow(region.getFirstRow());
				Cell firstCellOfFirstRow = firstRow.getCell(region.getFirstColumn());
				// 如果第一个单元格的是字符串
				firstCellOfFirstRow.setCellType(Cell.CELL_TYPE_STRING);
				String value =firstCellOfFirstRow.getStringCellValue();
				hssfSheet.removeMergedRegion(i);
				// 设置第一行的值为，拆分后的每一行的值
				for (Row row : hssfSheet) {
					for (Cell cell : row) {
						if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cell.setCellValue(value);
						}
					}
				}
			}
		} else if ("excel2007".equalsIgnoreCase(type)) {
			XSSFSheet xssfSheet = (XSSFSheet) sheet;
			// 遍历sheet中的所有的合并区域
			for (int i = xssfSheet.getNumMergedRegions() - 1; i >= 0; i--) {
				CellRangeAddress region = (CellRangeAddress) xssfSheet.getMergedRegion(i);
				Row firstRow = xssfSheet.getRow(region.getFirstRow());
				Cell firstCellOfFirstRow = firstRow.getCell(region.getFirstColumn());
				// 如果第一个单元格的是字符串
				firstCellOfFirstRow.setCellType(Cell.CELL_TYPE_STRING);
				String value =firstCellOfFirstRow.getStringCellValue();
				xssfSheet.removeMergedRegion(i);
				// 设置第一行的值为，拆分后的每一行的值
				for (Row row : xssfSheet) {
					for (Cell cell : row) {
						if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cell.setCellValue(value);
						}
					}
				}
			}
		}

	}
	/**
	 * excel(XLS)修复表格
	 * 
	 * @throws IOException
	 */
	public void repairExcel(int sheetNum, String fileName) {
		loggerR.info("正在修复excel"+fileName);
		FileInputStream FileInputStream = iOstream.loadFile(fileName);
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(FileInputStream);
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
			iOstream.closeInputStream(FileInputStream);
			iOstream.saveOutStream(workbook, fileName, "excel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerR.error(e);
		}
	}
	/**
	 * excel修复表格
	 * 
	 * @throws IOException
	 */
	public void repairExcel2007(int sheetNum, String fileName) {
		loggerR.info("正在修复excel"+fileName);
		FileInputStream FileInputStream = iOstream.loadFile(fileName);
		try {
			XSSFWorkbook workbook;
			workbook = new XSSFWorkbook(FileInputStream);
			XSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				XSSFRow row = sheet.getRow(rowNum);
				if (row == null) {
					// sheet.shiftRows(rowNum+1,sheet.getLastRowNum(),-1);
					System.out.println("有空格");// 此处还有问题
				} else {
					row.setHeight((short) 500);
				}
			}
			iOstream.closeInputStream(FileInputStream);
			iOstream.saveOutStream(workbook, fileName, "excel2007");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerR.error(e);
		}
	}
	/**
	 * excel(XLS)设置格式和字体
	 * 
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle setCellFont(HSSFWorkbook workbook) {
		loggerR.info("正在excel格式字体");
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
	 * 设置excel的字体格式
	 * 
	 * @param workbook
	 * @return
	 */
	public XSSFCellStyle setCellFont2007(XSSFWorkbook workbook) {
		loggerR.info("正在设置excel格式字体");
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("微软雅黑");
		cellStyle.setFont(font);
		return cellStyle;
	}


}
