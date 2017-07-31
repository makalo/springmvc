package com.huawei.tools.change;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class IOstream {
	public static Logger loggerIO= Logger.getLogger(IOstream.class);
	/**
	 * 产生输入流
	 * 
	 * @throws FileNotFoundException
	 */
	public FileInputStream loadFile(String path) {
		FileInputStream FileInputStream=null;
		try {
			FileInputStream = new FileInputStream("D:\\luceneData\\" + path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			loggerIO.error(e);
		}
		return FileInputStream;
	}

	/**
	 * 关闭输入文件流
	 * 
	 * @throws IOException
	 */
	public void closeInputStream(FileInputStream FileInputStream) {
		
		try {
			FileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerIO.error(e);
		}
	}

	/**
	 * 保存输出流
	 * 
	 * @throws IOException
	 */
	public void saveOutStream(Object op, String fileName, String tpye) {
		try {
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
		} catch (Exception e) {
			// TODO: handle exception
			loggerIO.error(e);
		}

	}
}
