package gov.nih.nci.evs.wusage.object;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HostEntity implements Comparable<HostEntity>{
	private String host;
	private String country;
	private String city;
	private String pages_st;
	private int pages;
	private String hits_st;
	private int hits;
	private String bandwidth_st;
	private Number bandwidth_num;
	private String lastVisit_st;
	private Date lastVisit_date;
	private String domain;
	private String hostName;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
		this.domain="Unknown";
		this.hostName="Unknown";
		// do host lookup. Discover domain
		//TODO, move whois lookup later, so we don't have to hit the whois server so much
		if (isNumeric(host)){
		whois_lookup();}
		
		
		//if whois could not find a domain, and the host is not a number,
		//  then grab the domain from the end of the host address.
		if (this.domain.equals("Unknown") && (!isNumeric(host))) {
			
			try {
				int domainStart = host.lastIndexOf(".");
				int domainEnd = host.length();
				if (domainStart > 0) {
					String domainTemp = host.substring(domainStart + 1,
							domainEnd);
					setDomain(domainTemp);
				}
				// possibly run against a lookup table, like the process
				// scripts, to get a better hostName
			} catch (Exception e) {
				setDomain("Unknown");
			}
			compareToHostList();
		}

		
//		System.out.println(this.domain + " " + this.host + " " + this.hostName);
		//TODO use hostlist in config, like in scripts

	}
	
	private void compareToHostList(){
		//check if hostName is unknown
		//if so, check domain. Do switch for gov/net/org/com/edu against properties
		//If domain other or unknown check against all properties is a specified order.
		try {
			if (this.domain.equals("gov")){
				Properties govProp = new Properties();
				govProp.load(new FileInputStream("./config/gov.properties"));
				Set keys = govProp.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = govProp.getProperty(lookup);
						break;
					}
				}
			}
			else if (this.domain.equals("edu")){
				Properties eduProp = new Properties();
				eduProp.load(new FileInputStream("./config/edu.properties"));
				Set keys = eduProp.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = eduProp.getProperty(lookup);
						break;
					}
				}
			}
			else if (this.domain.equals("net")||this.domain.equals("com")){
				Properties comProp = new Properties();
				comProp.load(new FileInputStream("./config/com.properties"));
				Set keys = comProp.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = comProp.getProperty(lookup);
						break;
					}
				}
			}else if (this.domain.equals("org")){
				Properties orgProp = new Properties();
				orgProp.load(new FileInputStream("./config/org.properties"));
				Set keys = orgProp.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = orgProp.getProperty(lookup);
						break;
					}
				}
			}else {
				Properties allProp = new Properties();
				
				allProp.load(new FileInputStream("./config/gov.properties"));
				Set keys = allProp.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = allProp.getProperty(lookup);
						this.domain="gov";
						break;
					}
				}
				if(this.hostName.equals("Unknown")){
				allProp.load(new FileInputStream("./config/edu.properties"));
				keys = allProp.keySet();
				iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = allProp.getProperty(lookup);
						this.domain = "edu";
						break;
					}
				}}
				if(this.hostName.equals("Unknown")){
				allProp.load(new FileInputStream("./config/com.properties"));
				keys = allProp.keySet();
				iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = allProp.getProperty(lookup);
						this.domain = "com";
						break;
					}
				}}
				if(this.hostName.equals("Unknown")){
				allProp.load(new FileInputStream("./config/org.properties"));
				keys = allProp.keySet();
				iter = keys.iterator();
				while(iter.hasNext()){
					String lookup = iter.next();
					if (host.contains(lookup))
					{
						this.hostName = allProp.getProperty(lookup);
						this.domain="org";
						break;
					}
				}}
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public boolean equalsHostName(String inHostName){
		return hostName.equalsIgnoreCase(inHostName);
	}

	private void setDomain(String inDomain) {
		this.domain = inDomain;
	}

	public String getDomain() {
		return this.domain;
	}

	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str.substring(0, 1));
		} catch (NumberFormatException e) {
			return false;
		}
		return true;

	}

	private void whois_lookup() {
		String response = whois();

		if (response != null) {
			whois_parser(response);
		} else {
			this.hostName = "Unknown";
			this.domain = "Unknown";
		}
	}



	private String whois() {
		String serverName = System
				.getProperty("WHOIS_SERVER", "whois.arin.net");
		InetAddress server = null;
		try {
			server = InetAddress.getByName(serverName);
			Socket theSocket = new Socket(server, 43);
			Writer out = new OutputStreamWriter(theSocket.getOutputStream(),
					"8859_1");
			
			//pass in the current host to the whois
			out.write(this.host + "\r\n");
			out.flush();

			InputStream in = new BufferedInputStream(theSocket.getInputStream());
			int c;
			StringBuffer response = new StringBuffer();
			while ((c = in.read()) != -1) {
				// System.out.write(c);
				response.append((char) c);
				// response.append("\r\n");
			}
			return response.toString();
			// whois_parser(response.toString());
		} catch (UnknownHostException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void whois_parser(String line) {
		boolean nameFound = false;
		boolean emailFound = false;
		String regexName = "OrgName: .*";
		String regexEmail = "OrgTechEmail: .*";
		Pattern pName = Pattern.compile(regexName);
		Pattern pEmail = Pattern.compile(regexEmail);
		String[] whois_results = line.split("\n");
		for (int i = 0; i < whois_results.length; i++) {

			Matcher m = pName.matcher(whois_results[i]);
			if (m.matches()) {
				// System.out.println(whois_results[i]);
				nameFound = true;
				// Extract the name and set it to host
				int colonLoc = whois_results[i].indexOf(":");
				int end = whois_results[i].length();
				this.hostName = whois_results[i].substring(colonLoc + 1, end)
						.trim();
			}
			m = pEmail.matcher(whois_results[i]);
			if (m.matches()) {
				// System.out.println(whois_results[i]);
				emailFound = true;
				// extract the domain and set it
				try {
					int domainStart = whois_results[i].lastIndexOf(".");
					int domainEnd = whois_results[i].length();
					String domainTemp = whois_results[i].substring(
							domainStart + 1, domainEnd);
					setDomain(domainTemp);
					// possibly run against a lookup table, like the process
					// scripts, to get a better hostName
				} catch (Exception e) {
					setDomain("Unknown");
				}
			}
		}
		if (!nameFound) {
			this.hostName = "Unknown";
		}
		if (!emailFound) {
			this.domain = "Unknown";
		}

	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPages_st() {
		return this.pages_st;
	}

	public void setPages_st(String in_pages_st) {
		this.pages_st = in_pages_st;
		this.pages_st = this.pages_st.replace(",", "");
		// remove commas. Convert to int
		this.pages = new Integer(this.pages_st).intValue();
	}

	public String getHits_st() {
		return this.hits_st;
	}

	public void setHits_st(String in_hits_st) {
		this.hits_st = in_hits_st;
		this.hits_st = this.hits_st.replace(",", "");
		// remove commas. Convert to int
		this.hits = new Integer(this.hits_st).intValue();
	}

	public String getBandwidth_st() {
		return this.bandwidth_st;
	}

	public void setBandwidth_st(String in_bandwidth_st) {
		this.bandwidth_st = in_bandwidth_st;
		// examine suffix - GB/MB/KB/Bytes/<null> and convert to appropriate
		// scale int
		NumberFormat numFormat = NumberFormat.getInstance();
		try {
			if (this.bandwidth_st.contains("GB")) {
				this.bandwidth_st = this.bandwidth_st.replace(" GB", "");
				this.bandwidth_num = numFormat.parse(this.bandwidth_st);
				this.bandwidth_num = this.bandwidth_num.doubleValue() * 1048576;
			} else if (this.bandwidth_st.contains("MB")) {
				this.bandwidth_st = this.bandwidth_st.replace(" MB", "");
				this.bandwidth_num = numFormat.parse(this.bandwidth_st);
				this.bandwidth_num = this.bandwidth_num.doubleValue() * 1024;
			} else if (this.bandwidth_st.contains("KB")) {
				this.bandwidth_st = this.bandwidth_st.replace(" KB", "");
				this.bandwidth_num = numFormat.parse(this.bandwidth_st);
			} else if (this.bandwidth_st.contains("Bytes")) {
				this.bandwidth_st = this.bandwidth_st.replace(" Bytes", "");
				this.bandwidth_num = numFormat.parse(this.bandwidth_st);
				this.bandwidth_num = this.bandwidth_num.doubleValue() / 1024;
			} else {
				this.bandwidth_num = 0;
			}
		} catch (ParseException e) {
			System.out.println("Could not parse bandwidth into number "
					+ this.bandwidth_st);
		}

	}

	public String getLastVisit_st() {
		return this.lastVisit_st;
	}

	public void setLastVisit_st(String in_lastVisit_st) {
		this.lastVisit_st = in_lastVisit_st;
		// parse date string and convert to date.
		// incoming example: 27 Mar 2012 - 19:56
		try {
			this.lastVisit_st = this.lastVisit_st.substring(0, 11);
			this.lastVisit_date = new Date(this.lastVisit_st);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.lastVisit_st="";
			this.lastVisit_date = new Date();
		}
	}

	public int getPages() {
		return this.pages;
	}

	public int getHits() {
		return this.hits;
	}

	public Double getBandwidth_dou() {
		return this.bandwidth_num.doubleValue();
	}

	public Date getLastVisit_date() {
		return this.lastVisit_date;
	}
	
	public String getHostName(){
		return hostName;
	}


	
	public int compareTo(HostEntity o) {
		if ((compareDomain(o.getDomain())==0) && (compareHostName(o.getHostName())==0))
		return 0;
		else if (compareDomain(o.getDomain())==0){
			return compareHostName(o.getHostName());
		}
		return compareDomain(o.getDomain());
	}


	public int compareDomain(String inDomain){
		return domain.compareToIgnoreCase(domain);
	}
	

	public int compareHostName(String inHostName){
		return hostName.compareToIgnoreCase(inHostName);
	}
	
	
}
