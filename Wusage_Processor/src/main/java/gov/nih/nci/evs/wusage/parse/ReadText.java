package gov.nih.nci.evs.wusage.parse;

public class ReadText {

	public ReadText() {
		// TODO Auto-generated constructor stub
	}

	public ReadText(String text){
		
		String hostText = RemoveHeaders(text);
	}
	
	public static String RemoveHeaders(String text){
		String bodyText = "";
		/*Last Update: 	18 Mar 2013 - 07:16	
Reported period:	- Year -2012 OK
Back to main page
 
Filter :		  	Exclude filter :		 OK 	 
Hosts 	 
Total : 313	GeoIP
Country	GeoIP
City	Pages	Hits	Bandwidth	Last visit
ncias-d815-v.nci.nih.gov	United States*/
		
		int endHeader = text.indexOf("visit");
		bodyText = text.substring(endHeader+5);		
		return bodyText;
	}
}
