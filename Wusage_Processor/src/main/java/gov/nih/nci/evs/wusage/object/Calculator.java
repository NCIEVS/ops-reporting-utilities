package gov.nih.nci.evs.wusage.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class Calculator {



   private static double calculateBandwidth(Vector<HostEntity> entities){
		Iterator<HostEntity> iter = entities.iterator();
		double bandwidth=0.0;
		while (iter.hasNext()){
			bandwidth = bandwidth + iter.next().getBandwidth_dou();
		}
		return bandwidth;
	}
   
   public static TreeMap<String, Double> calculateTopTen(HashMap<String,Vector<HostEntity>> entityCollection){
	   Map<String,Double> bandwidthByVector = new HashMap<String, Double>();
	   Set<String> key = entityCollection.keySet();
	   Iterator<String> keyIter = key.iterator();
	   while(keyIter.hasNext()){
		   String keyString = keyIter.next();
		   Vector<HostEntity> byKeyVector = entityCollection.get(keyString);
		   Iterator<HostEntity> entityIter = byKeyVector.iterator();
		   Double bandwidth = 0.0;
		   while (entityIter.hasNext()){
			   bandwidth = bandwidth + entityIter.next().getBandwidth_dou();
		   }
		   bandwidthByVector.put(keyString, bandwidth);
	   }
	   
	   TreeMap<String, Double> orderedByBandwidth = EntitySorter.sortMapByBandwidth(bandwidthByVector); 
	   return orderedByBandwidth;

	   
   }
   
   public static TreeMap<String,Double> orderByBandwidth(TreeMap map){
	   return EntitySorter.sortMapByBandwidth(map);
   }
	

}
