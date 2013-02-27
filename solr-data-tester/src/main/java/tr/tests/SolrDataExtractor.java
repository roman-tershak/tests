package tr.tests;

import java.io.FileInputStream;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

public class SolrDataExtractor {

	public static void main(String[] args) throws Exception {
		String solrServerUrl = "http://localhost:8983/solr";
		HttpClient defaultHttpClient = new DefaultHttpClient();
		ConcurrentUpdateSolrServer solrServer = new ConcurrentUpdateSolrServer(solrServerUrl, defaultHttpClient, 10, 10);
		
		String q = "file-contents:when";
		SolrQuery solrQuery = new SolrQuery(q);
		QueryResponse response = solrServer.query(solrQuery);
		
//		System.out.println(response);
		NamedList<Object> namedList = response.getResponse();
		
		SolrDocumentList documentList = (SolrDocumentList) namedList.get("response");
		for (SolrDocument solrDocument : documentList) {
			for (Entry<String, Object> entry : solrDocument) {
				System.out.println(entry.getKey());
				Object value = entry.getValue();
				if (value instanceof String) {
					String str = (String) value;
					if (str.length() > 256) {
						str = str.substring(0, 256);
					}
					System.out.println(str);
				} else {
					System.out.println("\t\t\tnon strings also happen");
					System.out.println(value);
				}
			}
		}
		
//		for (Entry<String, Object> entry : namedList) {
//			System.out.println(entry.getValue("id"));
//			System.out.println(entry.getValue("file-name"));
//			System.out.println(entry.getValue("file-path"));
//			System.out.println(entry.getValue("file-contents"));
//			System.out.println(entry.getValue("file-size"));
//		}
	}
}
