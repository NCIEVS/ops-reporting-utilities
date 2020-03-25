/* Rob Wynne, MSC
 * Fetch all EDQM data in JSON
 */

package gov.nih.nci.evs.edqm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


@SuppressWarnings("deprecation")
public class FetchEdqmJson {
	
	PrintWriter pw = null;
	
	public static void main(String args[]) {
		FetchEdqmJson fetcher = new FetchEdqmJson();
		fetcher.configure();
		fetcher.run();		
	}
	
	public void run() {
		try {

			String decompileWarning = "DECOMPILE WARNING: The key below is owned by the developer of this code."
					+ " Use without the knowledge of both the developer and NCI/NIH violates EDQM, and the MIT License.";
			
			//roll your own
			String secret = "********";
			Vector<String> codes = new Vector<String>();
			
			//TODO: Make the call to get all codes
			codes.add("SOM");
			codes.add("BDF");
			codes.add("RCA");
			codes.add("TRA");
			codes.add("ISI");
			codes.add("AME");
			codes.add("PDF");
			codes.add("UOP");
			codes.add("ROA");
			codes.add("PAC");
			codes.add("CON");
			codes.add("CLO");
			codes.add("DEV");
			codes.add("CDF");
			codes.add("PFT");
			codes.add("CMT");
			codes.add("CMP");
//			codes.add("MAP");
			codes.add("FIL");

			//TODO: generate date without param
			//		 and method/postfix via config file
			String httpVerb = "GET";
			String method = "/standardterms/api/v1/basic_data_by_class/";
			String methodPostfix = "/1/1";
			String host = "standardterms.edqm.eu";
			String date = getServerTime();
      		String parameters = "?tags=1";
			
			pw.print("[");
			pw.flush();
			
			for(int i=0; i < codes.size(); i++) {

				String message = httpVerb + "&" + method + codes.elementAt(i) + methodPostfix + "&" + host + "&" + date;
				
//				System.out.println(message);

				Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
				SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
				sha512_HMAC.init(secret_key);

				String hash = Base64.encodeBase64String(sha512_HMAC.doFinal(message.getBytes()));
				int hashEnd = hash.length();
				hash = hash.substring(66, hashEnd);
				
				@SuppressWarnings("resource")
				HttpClient client = new DefaultHttpClient();
				HttpUriRequest request = RequestBuilder.get().setUri("https://standardterms.edqm.eu/standardterms/api/v1/basic_data_by_class/" + codes.elementAt(i) + "/1/1" + parameters)
						.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
						.setHeader("X-STAPI-KEY","wynner@mail.nih.gov|" + hash)
						.setHeader("Accept-Charset", "utf-8")
						.setHeader("Date", date)
						.build();
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				
				String responseString = EntityUtils.toString(entity, "UTF-8");
				if( i < codes.size()-1) {
					pw.print(responseString + ",");
					pw.flush();
				}
				else {
					pw.print(responseString);
					pw.flush();
				}
				
				System.out.println(codes.elementAt(i) + " fetched.");
				// Don't stress them
				Thread.sleep(2000);
			}
			
			pw.print("]");
			pw.flush();
			pw.close();
			
		}
		catch(Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}		
	}
	
	private void configure() {
		File file = new File("EDQM.json");
		if( !file.exists() ) {
			try {
				if(file.createNewFile()) {
					pw = new PrintWriter(file);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Please remove or rename your last EDQM.json");				
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Please remove or rename your last EDQM.json");
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Please remove or rename your last EDQM.json");
			System.exit(0);
		}
	}	
	
	/* Credit to Hannes R.
	 * https://stackoverflow.com/questions/7707555/getting-date-in-http-format-in-java 
	 */
	String getServerTime() {
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(
	        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dateFormat.format(calendar.getTime());
	}
}
