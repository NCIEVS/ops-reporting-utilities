package gov.nih.nci.evs.wusage.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class EntitySorter {
	
//	Vector<String> domains;
//	Vector<String> hostNames;
//	Vector<Entity> entities = new Vector<Entity>();
//	HashMap<String,Vector<Entity>> Collection;

	public EntitySorter() {
		//Start looping through and calculating stuff.
		//Store the results locally in preparation for writing to Excel
		//sort the results by domain and hostName

	}
	
	
	//TODO write calculation routines for the entities
	//Sort entities into Vectors or Maps by domain
	//Sort entities into Vectors or Maps by domain/host
	
	//Calculate total visits to location

	
	//Calculate total visitors to location
	
	//Calculate total bandwidth to location
	
	//Calculate total bandwidth by domain
	
	//Calculate total bandwidth by domain/host. Identify top 10
	
	//For duration, simply clean up data and display
	
	//For FTP, calculate total files downloaded for each folder
	
	public static HashMap<String,Vector<HostEntity>> groupByDomain(Vector<HostEntity> entities){
		//find out how many domains there are, create an array of domains
		//create Vectors of entities for each domain?  Or use tree node for sortability?
		HashMap<String,Vector<HostEntity>> Collection = new HashMap<String,Vector<HostEntity>>();
		Vector<String> domains = new Vector<String>();
		Iterator<HostEntity> iter = entities.iterator();
		while (iter.hasNext()){
			String domain = iter.next().getDomain();
			if (!domains.contains(domain)){
				domains.add(domain);
			}
		}

		//Create Vector of Vectors
		//iterate domains and create a Vector<Entity> for each
		//iterate entities and dump into the current iterated domain Vector<Entity>
		//add current domain Vector into Vector of Vectors
		
		Iterator<String> domainIter = domains.iterator();
		while (domainIter.hasNext()){
			String domain = domainIter.next();
			Vector<HostEntity> domainEntities = new Vector<HostEntity>();
			Iterator<HostEntity> entityIter = entities.iterator();
			while(entityIter.hasNext()){
				HostEntity entity = entityIter.next();
				if (entity.getDomain().equals(domain)){
					domainEntities.add(entity);
				}
			}
			Collection.put(domain, domainEntities);	
		}
		
		return Collection;
	}
	
	
	public static HashMap<String,Vector<HostEntity>> groupByHostName(Vector<HostEntity> entities){
		//find out how many domains there are, create an array of domains
		//create Vectors of entities for each domain?  Or use tree node for sortability?
		HashMap<String,Vector<HostEntity>> Collection = new HashMap<String,Vector<HostEntity>>();
		Vector<String> hostNames = new Vector<String>();
		Iterator<HostEntity> iter = entities.iterator();
		while (iter.hasNext()){
			String hostName = iter.next().getHostName();
			if (!hostNames.contains(hostName)){
				hostNames.add(hostName);
			}
		}

		//Create Vector of Vectors
		//iterate domains and create a Vector<Entity> for each
		//iterate entities and dump into the current iterated domain Vector<Entity>
		//add current domain Vector into Vector of Vectors
		
		Iterator<String> hostNameIter = hostNames.iterator();
		while (hostNameIter.hasNext()){
			String hostName = hostNameIter.next();
			Vector<HostEntity> hostNameEntities = new Vector<HostEntity>();
			Iterator<HostEntity> entityIter = entities.iterator();
			while(entityIter.hasNext()){
				HostEntity entity = entityIter.next();
				if (entity.getHostName().equals(hostName)){
					hostNameEntities.add(entity);
				}
			}
			Collection.put(hostName, hostNameEntities);	
		}
		
		return Collection;
	}

	
	
	/**
	 * Sort map by key.
	 * 
	 * @param hmap
	 *            the hmap
	 * @return the hash map< string, string>
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> sortMapByKey(HashMap<String, String> hmap) {

		HashMap map = new LinkedHashMap();
		if (hmap.size() < 1)
			return map;
		;
		List mapKeys = new ArrayList(hmap.keySet());
		// List mapValues = new ArrayList(hmap.values());
		TreeSet sortedSet = new TreeSet(mapKeys);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;
		for (int i = 0; i < size; i++) {
			// map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),
			// sortedArray[i]);
			map.put(sortedArray[i], hmap.get(sortedArray[i]));
		}
		return map;

	}

	// public boolean getIsAnonymous(String code) {
	// return api.getIsAnonymous(code);
	// }

	/**
	 * Sort map by value.
	 * 
	 * @param hmap
	 *            the hmap
	 * @return the hash map< string, string>
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> sortMapByValue(HashMap<String, String> hmap) {
		HashMap map = new LinkedHashMap();
		List mapKeys = new ArrayList(hmap.keySet());
		List mapValues = new ArrayList(hmap.values());
		hmap.clear();
		TreeSet sortedSet = new TreeSet(mapValues);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;
		// a) Ascending sort

		for (int i = 0; i < size; i++) {
			map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),
			        sortedArray[i]);
		}
		return map;
	}
	
	
	public static TreeMap<String, Double> sortMapByBandwidth(Map<String, Double> hmap) {


		BandwidthComparator comp = new BandwidthComparator(hmap);
		TreeMap<String,Double> newMap = new TreeMap<String,Double>(comp);
		newMap.putAll(hmap);
		return newMap;

	}
	



}
