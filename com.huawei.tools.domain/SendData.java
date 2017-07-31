package com.huawei.tools.domain;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SendData{
	public static Logger loggerSD = Logger.getLogger(SendData.class);
    public void writeJSON2Response(JSONObject json,HttpServletResponse response) {
		response.setContentType("text/json;charset=UTF-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(json);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerSD.error(e);
		}
	   }
    public void writeJsonArrayResponse(JSONArray jsonArray,HttpServletResponse response)
    {
    	  
        response.setContentType("text/html;charset=UTF-8");  
        response.setContentType("application/json");  
        PrintWriter pw;
		try {
			pw = response.getWriter();
			pw.print(jsonArray);
	        pw.flush();  
	        pw.close();
		} catch (IOException e) {
			loggerSD.error(e);
		}  
        
    }
    }





