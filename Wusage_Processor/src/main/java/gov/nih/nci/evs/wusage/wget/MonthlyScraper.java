package gov.nih.nci.evs.wusage.wget;

import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MonthlyScraper {

	public MonthlyScraper() {
		// TODO Auto-generated constructor stub
	}

	public static void homePage() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = webClient
				.getPage("http://htmlunit.sourceforge.net");

		final String pageAsXML = page.asXml();

		final String pageAsText = page.asText();

		webClient.closeAllWindows();

	}

	public static void getElements() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = webClient
				.getPage("http://htmlunit.sourceforge.net");
		final HtmlDivision div = page.getHtmlElementById("contentBox");
		final HtmlAnchor anchor = page.getAnchorByName("Where_to_find...");

		page.setFocusedElement(anchor);

		final String pageAsXML = anchor.asXml();

		final String pageAsText = anchor.asText();

		webClient.closeAllWindows();
	}

	public static void getElementByXpath() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = webClient
				.getPage("http://htmlunit.sourceforge.net");

		// get list of all divs
		final List<?> divs = page.getByXPath("//table");

		// get div which has a 'name' attribute of 'John'
		final HtmlDivision div = (HtmlDivision) page.getByXPath(
				"//table[@name='John']").get(0);

		webClient.closeAllWindows();
	}



	public static void getAllHosts(String username, String password) throws Exception{
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
				.getCredentialsProvider();
		credentialsProvider.addCredentials(username, password);

		HtmlPage page1 = webClient
				.getPage("https://sitestats.nci.nih.gov/awstats/awstats.pl?month=all&year=2012&config=lexevsapi60.nci.nih.gov&framename=mainright");


	}
	
	
	public static void getMonthlies(String username, String password) throws Exception{
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
				.getCredentialsProvider();
		credentialsProvider.addCredentials(username, password);

		HtmlPage page1 = webClient
				.getPage("https://sitestats.nci.nih.gov/awstats/awstats.pl?month=all&year=2012&config=lexevsapi60.nci.nih.gov&framename=mainright");


	}

}
