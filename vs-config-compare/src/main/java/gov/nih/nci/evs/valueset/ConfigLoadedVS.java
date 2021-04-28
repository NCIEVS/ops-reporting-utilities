package gov.nih.nci.evs.valueset;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;


public class ConfigLoadedVS {

//    public List<ValueSetEntry> valueSetEntryList = new ArrayList<ValueSetEntry>();
    public HashMap<String, String> valueSetURIMap = new HashMap<String,String>();
    public HashMap<String, URL> valueSetExcelMap = new HashMap<String,URL>();

    public void parseLine(String line) throws URISyntaxException, MalformedURLException {
        String[] values = line.split("\\|");
        ValueSetEntry entry = new ValueSetEntry();
        entry.vsName=values[0];
        entry.uri = new URI(values[1]);
        if(!values[2].equalsIgnoreCase("null")) {
            entry.excelURL = new URL(values[2]);
            valueSetExcelMap.put(entry.vsName,entry.excelURL);
        }
        if(!values[3].equalsIgnoreCase("null")) {
            entry.page = values[3];
        }
//        valueSetEntryList.add(entry);
        valueSetURIMap.put(entry.vsName, entry.uri.toString());

    }

    public class ValueSetEntry{
        public String vsName;
        public URL excelURL;
        public URI uri;
        public String page;
    }
}
