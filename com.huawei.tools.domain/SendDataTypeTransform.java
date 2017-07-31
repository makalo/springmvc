package com.huawei.tools.domain;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;  

public class SendDataTypeTransform{    
    // 将Map格式数据转换为JSON类型数据并返回，否则返回null  
    public static JSONObject listMapToJson(List<HashMap<String,String>> dataList,int totalNum) {  
        
    	JSONObject jsonObject=new JSONObject();
    	JSONArray jsonArray=new JSONArray();
    	for(HashMap<String, String> hashmap :dataList)
    	{
    		JSONObject jsonItem=new JSONObject();
    	    jsonItem=JSONObject.fromObject(hashmap);
    	    jsonArray.add(jsonItem);
    	}
    	jsonObject.put("total", totalNum);
    	jsonObject.put("rows", jsonArray);
		return jsonObject;
    }  
    public static JSONArray listStrTOjsonArray(List<String> attributes)
    {
    	JSONArray jsonArray = JSONArray.fromObject(attributes);
		return jsonArray;
    	 
    }
    public static JSONObject autoReplyJson()
    {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", "保存成功");
		return jsonObject;
    }
}
