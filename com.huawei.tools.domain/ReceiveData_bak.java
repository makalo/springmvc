package com.huawei.tools.domain;

import java.util.ArrayList;
import java.util.List;
public class ReceiveData {

	public List <String[]> getRequestList(String keys,String values)
	{
		String[] searchKeys= keys.split("\\.");
		String[] searchValues= values.split("\\.");
		List<String[]>listContent=new ArrayList<String[]>();
		listContent.add(searchValues);
		listContent.add(searchKeys);
		return (List <String[]>)listContent;
	}
}
