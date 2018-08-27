package gov.nih.nci.evs.wusage.excel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class DebugOutput {

	public DebugOutput() {
		// TODO Auto-generated constructor stub
	}

	public static void printTopTen(TreeMap<String, Double> topTen, String domain){
		//TODO debug output
		DecimalFormat df = new DecimalFormat("0.00");
		
		System.out.println("Top Ten by Domain: "+ domain);
		Set<String> hostSet = topTen.keySet();
		Iterator<String> hostIter = hostSet.iterator();
		int i=0;
		while(hostIter.hasNext()){
			String host = hostIter.next();
			Double bandwidth = topTen.get(host);
			String formatBandwidth = df.format(bandwidth);
			if(i < 10){
			System.out.println(host + "  " + formatBandwidth + " MB");
			}
			else{break;}
			i++;
		}
		System.out.println();
	}
	
	
}
