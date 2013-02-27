package tr.tests;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;


public class SolrDataUploader {

	public static void main(String[] args) throws Exception {
		CommonsHttpSolrServer solrServer = new CommonsHttpSolrServer("http://localhost:8983/solr");
		
		File directory = new File("C:\\Tests\\Apache Solr\\apache-solr-3.6.0\\docs\\api");
		String[] extensions = new String[] {"html"};
		@SuppressWarnings("unchecked")
		Iterator<File> fileIterator = FileUtils.iterateFiles(directory, extensions , true);
		
		int i = 0;
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", i);
			doc.addField("file-name", file.getName());
			doc.addField("file-path", file.getAbsolutePath());
			doc.addField("file-contents", IOUtils.toString(new FileInputStream(file)));
			doc.addField("file-size", file.length());
			solrServer.add(doc);
			
			if (i++ % 100 == 0) {
				solrServer.commit();
				System.out.println("Number of files committed: " + i);
			}
		}
		solrServer.commit();
	}

}
