package gov.nih.nci.evs.wusage;

//This is the main class for the Wusage_Processor app. It handles
//the configuration and control.

import gov.nih.nci.evs.wusage.excel.DebugOutput;
import gov.nih.nci.evs.wusage.excel.ExcelHandler;
import gov.nih.nci.evs.wusage.object.BandwidthComparator;
import gov.nih.nci.evs.wusage.object.Calculator;
import gov.nih.nci.evs.wusage.object.HostEntity;
import gov.nih.nci.evs.wusage.object.EntitySorter;

//import gov.nih.nci.evs.wusage.parse.ReadHTML;
import gov.nih.nci.evs.wusage.parse.ReadText;
import gov.nih.nci.evs.wusage.wget.MonthlyScraper;
import gov.nih.nci.evs.wusage.wget.SiteScraper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.krickert.wget.Wget;
import com.krickert.wget.WgetStatus;

public class WusageProcessor {

	// Figure out what servers to scrape
	// check the server config file list
	// take in dates and login

	// wget each server individually and dump into its own object

	// MAIN PROCESSING
	// Parse the html dump into columns
	// Pull out relevant data - Host/IP,country,region,Bandwidth(download), Last
	// visit
	// Sometimes the columns get messed up if there is unknown country or region
	// Parse Bandwidth and convert to KB

	// Check Host/IP and do a lookup against whois and/or name file
	// BOT removal
	// sort by domain and portion out to domain bins
	// Do subtotal of KB for each domain bin
	// sort each domain bin then group by name, total the KB
	//

	// Possibly write to Excel and insert chart?
	// Useful charts/tables
	// Visits, Visitors for each browser
	// KB per domain
	// Users from each domain, sorted by KB
	// duration of visits?? - comes from separate area of wusage page

	private String username;
	private String password;
	private String server;
	private String month;
	private String year;
	private enum dom {gov,edu,org,com,UNKNOWN}
	
	public static void main(String[] args) {
		// -u username
		// -p password
		// -s server - in case they don't want to use config
		// -m month - can be ALL
		// -y year

		
		try {
			WusageProcessor wu = new WusageProcessor();
			wu.configure(args);
			//TODO add parameter to identify what type of data I am reading
			//Is it the main table, or info on the duration of use, or ftp stats divided by folder
			
			wu.processHosts();
			

			wu.processMonthlies();
			

			//TODO read a different part of the page to get length of visit
			//TODO read a different part of page for FTP to get categorized stats
			
			//Write everything out to Excel
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processMonthlies(){
		//TODO read a different part of the page to get visits/visitors
		try {
			SiteScraper.getMonthlies(username, password, server, year, month);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processHosts(){

		
		//getHosts;
		try {
			//TOD get hosts
			String allHosts = SiteScraper.getAllHosts(username, password, server,year, month);
//			String hostBody = ReadText.RemoveHeaders(allHosts);
			String[] hostArray = allHosts.split("\n");
			
//			ReadHTML rhtml = new ReadHTML("./data/temp-lexevsapi60-2013-01-11.htm","./filteredhtm.txt");
//			Vector<Entity> entities = parseIntoEntities("./filteredhtm.txt");
			Vector<HostEntity> entities = parseIntoHostEntities(hostArray);
			
			
			ExcelHandler excelOut = new ExcelHandler(entities, "Hosts");
			
			
			
			//Start processing top ten by bandwidth
//			processOrderByBandwidth(entities);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	


	public void configure(String[] args) throws Exception {
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String option = args[i];
				if (option.equalsIgnoreCase("--help")) {
					printHelp();
				} else if (option.equalsIgnoreCase("-U")) {
					username = args[++i];
				} else if (option.equalsIgnoreCase("-P")) {
					password = args[++i];
				} else if (option.equalsIgnoreCase("-S")) {
					server = args[++i];
				} else if (option.equalsIgnoreCase("-M")) {
					month = args[++i];
				} else if (option.equalsIgnoreCase("-Y")) {
					year = args[++i];
				} else {
					printHelp();
				}

			}
		} else {
			printHelp(); // This will exit the program
		}
	}
	


	public void printHelp() {
		System.out.println("");
		// System.out.println("Usage: OWLScrubber [OPTIONS] ... [OWL] [OUTPUT FILE]");
		System.out.println("Usage: WusageProcessor [OPTIONS] ");
		System.out.println(" ");
		System.out.println("  -U \tCurrent ldap username");
		System.out.println("  -P, \tCurrent ldap password");
		System.out
				.println("  -S, \tServer to be tracked, as listed in sitestats");
		System.out
				.println("  -M, \tMonth for which to pull data.  Can use ALL");
		System.out.println("  -Y, \tYear for which to pull data - four digits");
		System.out.println("");
		System.exit(1);
	}
	
	
	public static Vector<HostEntity> parseIntoEntities(String[] hostArray){
		//create container to hold entities
		Vector<HostEntity> rowEntities = new Vector<HostEntity>();		
		for(int i = 0; i<hostArray.length; i++){
			String temp = hostArray[i];
			String[] temp1 = temp.split("\t");
			HostEntity ent = new HostEntity();
			ent.setHost(temp1[0]);
			ent.setCountry(temp1[1]);
			ent.setCity(temp1[2]);
			ent.setPages_st(temp1[3]);
			ent.setHits_st(temp1[4]);
			ent.setBandwidth_st(temp1[5]);
			ent.setLastVisit_st(temp1[6]);
			rowEntities.add(ent);
		}
		
		return rowEntities;
	}
	
	public static Vector<HostEntity> parseIntoHostEntities(String inputFile) {
		//create container to hold entities
		Vector<HostEntity> rowEntities = new Vector<HostEntity>();
		try{
		//start parsing the input file and loading entities.
		FileReader fr = new FileReader(inputFile);
		BufferedReader inputReader = new BufferedReader(fr);
		
		//Ready entity for writing to Excel
		String line = "";
		while((line=inputReader.readLine())!=null){
			String temp = line.substring(4);
			String[] temp1 = temp.split("\\.\\.\\.\\.+");
			HostEntity ent = new HostEntity();
			ent.setHost(temp1[0]);
			ent.setCountry(temp1[1]);
			ent.setCity(temp1[2]);
			ent.setPages_st(temp1[3]);
			ent.setHits_st(temp1[4]);
			ent.setBandwidth_st(temp1[5]);
			ent.setLastVisit_st(temp1[6]);
			rowEntities.add(ent);
		}
		
		}
		catch (FileNotFoundException e){
			System.out.println("File Not Found " + inputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return rowEntities;
	}
	
	public static Vector<HostEntity> parseIntoHostEntities(String[] inputArray) {
		//create container to hold entities
		Vector<HostEntity> rowEntities = new Vector<HostEntity>();
		try{
		//Ready entity for writing to Excel
		String line = "";
		for(int i = 0; i<inputArray.length; i++){
			line = inputArray[i];
//			String temp = line.substring(4);
			String[] temp1 = line.split("\t");
			if (temp1.length==7){
			HostEntity ent = new HostEntity();
			ent.setHost(temp1[0]);
			ent.setCountry(temp1[1]);
			ent.setCity(temp1[2]);
			ent.setPages_st(temp1[3]);
			ent.setHits_st(temp1[4]);
			ent.setBandwidth_st(temp1[5]);
			ent.setLastVisit_st(temp1[6]);
			rowEntities.add(ent);}
			else {
				System.out.println(line);
			}
		}
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return rowEntities;
	}

}
