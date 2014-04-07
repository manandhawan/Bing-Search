package com.searchtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BingTest {

	static String accountKey;
	static ArrayList<String> searchUrl;

	public static void main(String[] args) throws IOException {

		String searchText = "";
		System.out.println("Enter a query: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		searchText = in.readLine();
		searchText = searchText.replaceAll(" ", "%20");
		
		accountKey = "Uc172BmWxqKuh0M2lNcjv9UpAnBpwWPRdjXKNeXuC1Y=";
		byte[] accountKeyBytes = Base64
				.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);
		
		JSONParser parser = new JSONParser();
		URL url;
		
		try{
			long start = System.currentTimeMillis();
			url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + 
							searchText + "%27&$top=30&$format=JSON");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
	            sb.append(output);
	        }
			conn.disconnect();
			
			JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
			
			JSONObject d = (JSONObject) jsonObject.get("d");
			JSONArray results = (JSONArray) d.get("results");
			
			Iterator<?> i = results.iterator();
			int no = 0;
			searchUrl  = new ArrayList<String>();
			while(i.hasNext()){
				JSONObject innerObject = (JSONObject) i.next();
				searchUrl.add(innerObject.get("Url").toString());
				URL downloadUrl = new URL(innerObject.get("Url").toString());
				InputStream is;
				is = downloadUrl.openStream();
				int ptr = 0;
				StringBuffer buffer = new StringBuffer();
				while ((ptr = is.read()) != -1) {
				    buffer.append((char)ptr);
				}
				no++;
				writeToFile("File"+no+".html", buffer);
				System.out.println("File"+no+".html"+" written");
			}
			
			System.out.println(searchUrl.size() + "" + searchUrl);
			long end = System.currentTimeMillis();
			long totalTime = (end - start)/1000;
			System.out.println("Time taken: " + totalTime +" seconds");
			  
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String pFilename, StringBuffer pData) throws IOException {  
        BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
        out.write(pData.toString());  
        out.flush();  
        out.close();  
    } 
}