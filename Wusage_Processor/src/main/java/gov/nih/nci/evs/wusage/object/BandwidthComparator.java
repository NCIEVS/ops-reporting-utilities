package gov.nih.nci.evs.wusage.object;

import java.util.Comparator;
import java.util.Map;




public class BandwidthComparator implements Comparator {
	Map map;
	
	public BandwidthComparator(Map map){
		this.map = map;
	}

	
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
	}

}
