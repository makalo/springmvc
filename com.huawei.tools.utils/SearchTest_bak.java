package com.huawei.tools.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.tools.controller.SearchController;
import com.huawei.tools.domain.SendData;
import com.huawei.tools.domain.SendDataTypeTransform;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//有时候前台点击查询，没反应
@Controller

public class SearchTest {
	public static SearchController searchController = null;
	@SuppressWarnings("rawtypes")
	private static LinkedHashMap<String, List> allSearchDocResult = null;
	private static List<Document> singleSearchDocResult=null;
	
	
	@RequestMapping("/")
	private String Index() {
		searchController = new SearchController();
		
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
		return "index";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getlists", method = RequestMethod.GET)
	public void sendAndReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		String flag=request.getParameter("flag");
		if(flag.equals("a"))
		{
			String keys = request.getParameter("keys");
			String values = request.getParameter("values");
			String tag=request.getParameter("tag");
			if(tag.equals("y"))
			{
				// 接收数据
			    List<String[]> receiveListData = (List<String[]>) searchController.getDataToList(keys, values);
				// 查询结果
		        allSearchDocResult = (LinkedHashMap<String, List>) searchController.searchIndex(receiveListData);
		        singleSearchDocResult=(List<Document>) allSearchDocResult.entrySet().iterator().next().getValue();
		        if(singleSearchDocResult.size()!=0){
		             searchController.toJsonArraySent_t(response, singleSearchDocResult.get(0),allSearchDocResult.get("fileList"));
			    }else{
			    	JSONObject noReplayMsg = SendDataTypeTransform.noReplyJson();
			    	SendData sendData = new SendData();
					sendData.writeJSON2Response(noReplayMsg, response);
			    }
			    }
			else{
				String file=request.getParameter("file");
				if(!"default".equalsIgnoreCase(file))
				{
					singleSearchDocResult=(List<Document>) allSearchDocResult.get(file);
				}
				searchController.toJsonArraySent_t(response, singleSearchDocResult.get(0),allSearchDocResult.get("fileList"));
			}  
		}
		else if(flag.equals("b"))
		{
			int pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			int pageSize = Integer.parseInt(request.getParameter("pageSize"));
			List<HashMap<String, String>> nextSearchResult = searchController.searchResult(singleSearchDocResult, pageSize,
					pageNumber);
			
			searchController.toJsonSent(response, nextSearchResult, singleSearchDocResult.size());
		}
	}
	@RequestMapping(value = "getAllAttributes", method = RequestMethod.GET)
	public void getAttributes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		searchController.toJsonArraySent(response, searchController.test.reader);
	}
	@RequestMapping(value = "/editFile", method = RequestMethod.POST)
	public void change(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String saveItems = request.getParameter("saveItems");
		System.out.println(saveItems+"saveItems");
		searchController.changeFile(saveItems, "revise");
		searchController.autoJsonSent(response);
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
	@RequestMapping(value = "/deleteFileItems", method = RequestMethod.POST)
	public void delRow(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String deleteItems = request.getParameter("deleteItems");
		System.out.println(deleteItems);	
		searchController.changeFile(deleteItems, "delRow");	
		JSONArray arrayRow = JSONArray.fromObject(deleteItems);
		for (int i = 0; i < arrayRow.size(); i++) {
			JSONObject jsonObject = JSONObject.fromObject(arrayRow.get(i));
		    int pageNumber = Integer.parseInt(jsonObject.get("pageNumber").toString());
			int pageSize = Integer.parseInt(jsonObject.get("pageSize").toString());
			String delLoc=jsonObject.get("location").toString();
			String delFlieSheet=delLoc.split(":")[0]+":"+"sheet"+delLoc.split(":")[1];
			List<HashMap<String, String>> nextSearchResult = searchController.searchResult(singleSearchDocResult, pageSize,
					pageNumber);
			Iterator<HashMap<String, String>> singleResultIter = nextSearchResult.iterator();
			int delIndex=0;;
			while(singleResultIter.hasNext()){		
				if(singleResultIter.next().containsValue(delLoc)){
					singleSearchDocResult.remove(delIndex);
					allSearchDocResult.get(delFlieSheet).remove(delIndex);
					/*allSearchDocResult*/
				}
				delIndex++;
			}
		}

		searchController.autoJsonSent(response);
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
	@RequestMapping(value = "/submitItem", method = RequestMethod.POST)
	public void addRow(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String saveItems = request.getParameter("submitItem");
		searchController.changeFile(saveItems, "addRow");
		searchController.autoJsonSent(response);
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
	@RequestMapping(value = "/addAttr", method = RequestMethod.POST)
	public void addAttr(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String saveItems = request.getParameter("saveItems");
		searchController.changeFile(saveItems, "addAttr");
		searchController.autoJsonSent(response);
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
	@RequestMapping(value = "void3", method = RequestMethod.GET)
	public void closeToReIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		searchController.upload(request, response);
		searchController.autoJsonSent(response);
		List<File> list_file = searchController.getFiles();
		searchController.createIndexAll(list_file);
	}
}
