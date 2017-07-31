package com.huawei.tools.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.huawei.tools.dao.ReadFile;;
public class LuceneIndex {
	public static Logger loggerLI = Logger.getLogger(LuceneIndex.class);
	private IndexWriter writer = null;
	ReadFile read=null;
	public LuceneIndex(String indexStorePath) {
		try {
			read=new ReadFile();
			Directory dir = FSDirectory.open(new File(indexStorePath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(dir, iwc);
		} catch (Exception e) {
			loggerLI.error(e);
		}
	}
/**
 * 建立索引目录
 * @param file 需要建立索引的文件
 * @throws Exception
 */
	public void getDocument(File file){
		try {
			List<List<List<String>>> list_sheet =null;
			String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
			if("xlsx".equalsIgnoreCase(type) || "xls".equalsIgnoreCase(type)){
				if("xlsx".equalsIgnoreCase(type))
					list_sheet = (List<List<List<String>>>) read.readExcel2007(file);
				else
					list_sheet = (List<List<List<String>>>) read.readExcel(file);
				for(int i=0;i<list_sheet.size();i++)
		          {
					if (list_sheet.get(i)==null)
					{
						continue;
					}
		          	for (int j=0;j<((List<List<String>>) list_sheet.get(i)).size()-1;j++)
		          	{
		          		Document doc = new Document();
		          		String id=file.getName()+":"+i+":"+(j+1);
		          		String subId=file.getName()+":"+"sheet"+i;
		          		doc.add(new TextField("路径",id,Store.YES));
		          		doc.add(new TextField("子路径",subId,Store.YES));
		          		for (int n=0;n<((List<String>)((List<List<String>>) list_sheet.get(i)).get(j)).size()-1;n++)
		          		{   
		          			doc.add(new TextField(((List<String>)((List<List<String>>) list_sheet.get(i)).get(0)).get(n),
		          					((List<String>)((List<List<String>>) list_sheet.get(i)).get(j+1)).get(n), Store.YES));
		          		}
		          		writer.addDocument(doc);
		          	}
		          }
			}
			else if("txt".equalsIgnoreCase(type))
			{
				Document doc = new Document();
				String content=read.readTxt(file);
				doc.add(new TextField("content", content,Store.YES));
				writer.addDocument(doc);
			}
			else if("xml".equalsIgnoreCase(type))
			{
				List<HashMap<String, String>>listData=read.readXml(file);
				for (HashMap<String, String> listMap:listData)
				{
					Document doc = new Document();
					String subId=file.getName();
	          		doc.add(new TextField("子路径",subId,Store.YES));
	          		doc.add(new TextField("路径",file.getName()+":"+listMap.get("路径"),Store.YES));
					Iterator<Entry<String, String>> iterator=listMap.entrySet().iterator();
					while(iterator.hasNext())
					{
						Map.Entry<String,String> entry=iterator.next();
						if (entry.getKey().equals("路径"))
							continue;
						doc.add(new TextField(entry.getKey(), entry.getValue(),Store.YES));
					}
					writer.addDocument(doc);
				}
			}
		} catch (Exception e) {
			loggerLI.error(e);
		}
		
	}
	/**
	 * 关闭索引
	 * @throws Exception
	 */
	public void close(){
		try {
			writer.close();
		} catch (IOException e) {
			loggerLI.error(e);
		}
	}
}

