package gov.nih.nci.evs.wusage.wget;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import sun.misc.BASE64Encoder;
import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class SiteScraper {
	



	public SiteScraper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void get(String url, String username, String password){
		try{
			URL location = new URL(url);
			java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
			
			String login = username+":"+password;
			String encodedLogin = new BASE64Encoder().encode(login.getBytes());
			URLConnection connection = location.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + encodedLogin);
			connection.connect();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							connection.getInputStream()));
			String inputLine;
			String data = null;
			while ((inputLine = in.readLine()) != null) 
				data += inputLine + "\n";
			in.close();}

		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static String getAllHosts(String username, String password, String url, String year) throws Exception{
		//pass in month, year
		return getAllHosts(username, password,url,year,"ALL");
	}
	
	public static String getAllHosts(String username, String password, String url, String year, String month) throws Exception{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		WebClient webClient = new WebClient();
		
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.setPrintContentOnFailingStatusCode(false);
		
		DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
				.getCredentialsProvider();
		credentialsProvider.addCredentials(username, password);

		String searchString = "https://sitestats.nci.nih.gov/awstats/awstats.pl?month="+month+"&year="+year+"&config="+ url + ".nci.nih.gov&framename=mainright&output=allhosts";
		HtmlPage page = webClient
				.getPage(searchString);
		
		//TODO detect if get returns an error message

		HtmlElement ele = page.getBody();
		
		webClient.closeAllWindows();
		String fullText = ele.asText();
		int endHeader = fullText.indexOf("visit");
		String bodyText = fullText.substring(endHeader+7);
		return bodyText;
//		return ele.asText();

	}
	
	public static void getMonthlies(String username, String password, String url, String year) throws Exception{
		//pass in month, year
		getMonthlies(username, password,url,year,"ALL");
		
	}

	public static void getMonthlies(String username, String password, String url, String year, String month) throws Exception{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		//TODO pass in month, year
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
				.getCredentialsProvider();
		credentialsProvider.addCredentials(username, password);

		String searchString = "https://sitestats.nci.nih.gov/awstats/awstats.pl?month="+month+"&year="+year+"&config="+ url + ".nci.nih.gov&framename=mainright";
		HtmlPage page = webClient
				.getPage(searchString);
		final String pageAsXML = page.asXml();

//		final String pageAsText = page.asText();

		webClient.closeAllWindows();
	}
	
	public static void getEVSFTP(String username, String password, String month, String year){
		//allEVS = extra 1
		//CDISC = extra 2
		//FDA = extra 3
		//FMT = extra 4
		//CTCAE = extra 5
		//NCIm = extra 6
		//NCIt = extra 7
		//NCPDP = extra 8
		//NCD-RT = extra 9
		//NICHD = extra 10
		
		String url = "ftp1.nci.nih.gov";
						
				
	}
}


