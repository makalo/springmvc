package com.huawei.tools.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.dom4j.DocumentException;

import com.huawei.tools.change.AdjustmentCenter;
import com.huawei.tools.dao.DataConnection;
import com.huawei.tools.domain.ReceiveData;
import com.huawei.tools.domain.SendData;
import com.huawei.tools.domain.SendDataTypeTransform;
import com.huawei.tools.domain.Upload;
import com.huawei.tools.service.Attribute;
import com.huawei.tools.service.LuceneIndex;
import com.huawei.tools.service.LuceneSearch;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SearchController {

	public static Logger loggerSC = Logger.getLogger(SearchController.class);
	public final static String DATA_FILE_PATH = "D:/luceneData"; // 索引的文件的存放路径
	public final static String INDEX_FILE_PATH = "D:/luceneIndex"; // 生成索引文件的存放位置
	public LuceneSearch test = null;

	public SearchController() {
		test = new LuceneSearch(INDEX_FILE_PATH);
	}

	/**
	 * 获取指定路径下需要索引的文件列表
	 * 
	 * @return List<File>
	 */
	public List<File> getFiles() {
		DataConnection dataCon = new DataConnection();
		return dataCon.getFileList(DATA_FILE_PATH);

	}

	/**
	 * 对所有文件建立索引,目前支持xls，xlxs，xml文件格式
	 * 
	 * @param fileList
	 *            想要执行的文件的列表
	 * @param queryAttrs
	 *            索引属性与索引值的Map映射
	 * @return
	 * @throws Exception
	 */
	public boolean createIndexAll(List<File> fileList){
		boolean flag=false;
		//removeAll(INDEX_FILE_PATH);
		LuceneIndex indexer = new LuceneIndex(INDEX_FILE_PATH);
		long start = System.currentTimeMillis();
		for (File file:fileList)
		{
			indexer.getDocument(file);
			loggerSC.info("正在建立索引 : " + file + "");
		}
		long end = System.currentTimeMillis();
		loggerSC.info("建立索引用时" + (end - start) + "毫秒");
		indexer.close();
		return flag;
	}
	/**
	 * 查找索引，返回符合条件的文件
	 * 
	 * @param text
	 *            查找的字符串
	 * @return 符合条件的文件List
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, List> searchIndex(List<String[]> listContent){
		// LuceneSearch test = new LuceneSearch(INDEX_FILE_PATH);
		Occur[] occurs = new Occur[listContent.get(0).length];
		for (int i = 0; i < listContent.get(0).length; i++) {
			occurs[i] = Occur.MUST;
		}
		TopDocs results = null;
		results = test.search(listContent.get(0), listContent.get(1), occurs);
		ScoreDoc[] h = results.scoreDocs;
		List<String> listFile = new ArrayList<String>();
		// List<Document> listDoc=new ArrayList<Document>();
		LinkedHashMap<String, List> hashFile = new LinkedHashMap<String, List>();
        try {
    	   for (int i = 0; i < h.length; i++) {
   			Document doc = test.searcher.doc(h[i].doc);
   			String subID = doc.get("子路径");
   			if (listFile.size() == 0) {
   				listFile.add(subID);
   			} else {
   				boolean flag = true;
   				for (String string : listFile) {
   					if (string.equalsIgnoreCase(subID)) {
   						flag = false;
   					}
   				}
   				if (flag) {
   					listFile.add(subID);
   				}
   			}
   			// listDoc.add(test.searcher.doc(h[i].doc));
   		}
   		for (String string : listFile) {
   			List<Document> listDoc = new ArrayList<Document>();
   			for (int i = 0; i < h.length; i++) {
   				Document doc = test.searcher.doc(h[i].doc);
   				String subID = doc.get("子路径");
   				if (string.equalsIgnoreCase(subID)) {
   					listDoc.add(doc);
   				}
   			}
   			hashFile.put(string, listDoc);
   		}
   		hashFile.put("fileList", listFile);
		} catch (Exception e) {
			loggerSC.error(e);
		}
		return hashFile;
	}

	
	/**
	 * 对查询结果打分排序，并打印出来，后期会存放在一个容器里传给前端
	 * 
	 * @param results
	 * @param pageSize
	 *            用于翻页设计
	 * @param currentPage用于翻页设计
	 * @param searcher
	 * @param reader
	 */
	public List<HashMap<String, String>> searchResult(List<Document> listDoc, int pageSize, int pageNum) {
		List<HashMap<String, String>> listRow = new ArrayList<HashMap<String, String>>();
		int begin = pageNum - 1;
		int end = Math.min(begin + pageSize, listDoc.size());
		if (listDoc.size() == 0) {
			loggerSC.info("对不起，没有找到您要的结果。");
		} else {
			for (int i = begin; i < end; i++) {
				LinkedHashMap<String, String> hash = new LinkedHashMap<String, String>();
				try {
					Document doc = listDoc.get(i);
					List<String> listAttribute = (List<String>) new Attribute().attributeListDoc(doc);
					hash.put("id", i + "");
					for (int j = 0; j < listAttribute.size(); j++) {
						hash.put(listAttribute.get(j), doc.get(listAttribute.get(j)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				listRow.add(hash);
			}
		}
		return listRow;
	}

	/**
	 * 删除INDEX_FILE_PATH目录下的所有文件，
	 */
	public void removeAll(String path) {
		File fileindex = new File(path);
		if (deleteDir(fileindex)) {
			fileindex.mkdir();
		} else {
			fileindex.mkdir();
		}
	}

	/**
	 * 删除文件目录下的所有文件，
	 * 
	 * @param file
	 *            要删除的文件目录
	 * @return 如果全部删除返回true.
	 */
	public boolean deleteDir(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
		}
		file.delete();
		return true;
	}

	/**
	 * 转换成json数据，并发送出去
	 * 
	 * @throws Exception
	 */
	public void toJsonSent(HttpServletResponse response, List<HashMap<String, String>> dataList, int totalNum)
			{
		if(totalNum==0)
		{
			autoJsonSent(response);
		}
		else{
			JSONObject json = SendDataTypeTransform.listMapToJson(dataList, totalNum);
			SendData sendData = new SendData();
			sendData.writeJSON2Response(json, response);
		}	
	}

	/**
	 * 转换成jsonArray数据，并发送出去
	 * 
	 * @throws IOException
	 */
	public void toJsonArraySent(HttpServletResponse response, IndexReader reader){
		List<String> listAttribute = (List<String>) new Attribute().attributeListAll(reader);
		//String[] attribute = (String[]) listAttribute.toArray(new String[listAttribute.size()]);
		JSONArray jsonArray = SendDataTypeTransform.listStrTOjsonArray(listAttribute);
		SendData sendData = new SendData();
		sendData.writeJsonArrayResponse(jsonArray, response);
	}
	public void toJsonArraySent_t(HttpServletResponse response,Document doc,List<String> fileList)
	{
		List<String> listAttribute = (List<String>) new Attribute().attributeListDoc(doc);
		//String[] attribute = (String[]) listAttribute.toArray(new String[listAttribute.size()]);
		JSONArray jaAttr = SendDataTypeTransform.listStrTOjsonArray(listAttribute);
		JSONArray jaFile = SendDataTypeTransform.listStrTOjsonArray(fileList);
		JSONObject json=new JSONObject();
		json.put("files", jaFile);
		json.put("attributes",jaAttr);
		SendData sendData = new SendData();
		sendData.writeJSON2Response(json, response);
	}

	/**
	 * 自动发送
	 * 
	 * @throws Exception
	 */
	public void autoJsonSent(HttpServletResponse response){
		JSONObject json = SendDataTypeTransform.autoReplyJson();
		SendData sendData = new SendData();
		sendData.writeJSON2Response(json, response);
	}
	public void transfAndSend(HttpServletResponse response,Object object,String dataType)
	{
		SendData sendData = new SendData();
		if("listMap".equalsIgnoreCase(dataType))
		{
			//JSONObject json = SendDataTypeTransform.listMapToJson(dataList, totalNum,listFile);
			//sendData.writeJSON2Response(json, response);
		}
		else if ("listStr".equalsIgnoreCase(dataType))
		{
			//JSONArray jsonArray = SendDataTypeTransform.listStrTOjsonArray(listAttribute);
			//sendData.writeJsonArrayResponse(jsonArray, response);
		}
		else if ("auto".equalsIgnoreCase(dataType))
		{
			JSONObject json = SendDataTypeTransform.autoReplyJson();
			sendData.writeJSON2Response(json, response);
		}
	}

	/**
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public List<String[]> getDataToList(String keys, String values) {
		ReceiveData data = new ReceiveData();
		return (List<String[]>) data.getRequestList(keys, values);

	}

	/**
	 * @throws DocumentException
	 * @throws IOException
	 * 
	 */
	public void changeFile(String saveItems, String opType){
		AdjustmentCenter change = new AdjustmentCenter();
		List<Integer> listRow=new ArrayList<Integer>();
		JSONArray arrayRow = JSONArray.fromObject(saveItems);
		System.out.println(arrayRow.size());
		for (int i = 0; i < arrayRow.size(); i++) {
			JSONObject jsonObject = JSONObject.fromObject(arrayRow.get(i));
			System.out.println(jsonObject);
			change.init(jsonObject,arrayRow.size());
			change.changeControler(opType,listRow);
		}
	}
	/**
	 * 上传
	 * @throws ServletException 
	 * @throws IOException 
	 */
	public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		Upload upload=new Upload();
		upload.doPost(request, response);
	}

}
