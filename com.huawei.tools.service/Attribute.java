package com.huawei.tools.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;

public class Attribute {
	public static Logger loggerA = Logger.getLogger(Attribute.class);
	/**
	 * 获取索引里面所有关键字属性
	 * @return
	 * @throws IOException
	 */
	public List<String> attributeListAll(IndexReader reader)
	{   List<String> listAll=new ArrayList<String>();
		Fields fields;
		try {
			fields = MultiFields.getFields(reader);
			Iterator<String> fieldsIterator = fields.iterator();
			while(fieldsIterator.hasNext()){
	            String field = fieldsIterator.next();
	            listAll.add(field);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loggerA.error(e);
		}
		return listAll;
	}
	public List<String> attributeListDoc(Document doc)
	{   List<String> listDoc=new ArrayList<String>(); 
		List<IndexableField> fields = doc.getFields();
		for(IndexableField field:fields)
		{
			listDoc.add(field.name());
		}
		return listDoc;		
	}
}
