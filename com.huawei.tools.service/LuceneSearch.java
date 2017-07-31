package com.huawei.tools.service;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
 
public class LuceneSearch {
    public static Logger loggerLS= Logger.getLogger(LuceneSearch.class);
	public IndexSearcher searcher = null;
	private Analyzer analyzer=null;
    public IndexReader reader=null;

	public LuceneSearch(String indexStorePath) {
		try {
			FSDirectory fsDirectory =FSDirectory.open(new File(indexStorePath));
			reader = DirectoryReader.open(fsDirectory);
			searcher = new IndexSearcher(reader);
		} catch (Exception e) {
			e.printStackTrace();  
			loggerLS.error(e);
		}
	}
    /**
     * 返回查询结果
     * @param keyword关键词
     * @param field关键词属性
     * @return
     */
	public final TopDocs search(String [] stringQuery,String[] fields,Occur[] occ) {
		for (String str:stringQuery)
		{
			loggerLS.info("正在检索关键字"+str);
		}
		try {
			analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
			//QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);
			//query = parser.parse(keyword);
			Date start = new Date();
			Query query = MultiFieldQueryParser.parse(Version.LUCENE_4_9,stringQuery, fields, occ, analyzer);
			TopDocs results = searcher.search(query,100);
			Date end = new Date();
			loggerLS.info("检索完成，用时" + (end.getTime() - start.getTime()) + "毫秒");
			return results;
		} catch (Exception e) {
			loggerLS.error(e);
		}
		return null;
	}
}
