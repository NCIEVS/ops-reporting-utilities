package gov.nih.nci.evs.valueset;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

//This object holds the data loaded from the value_set_config_report.txt

public class ConfigLoadedVS {

    public HashMap<String, ValueSetEntry> valueSetExcelMap = new HashMap<String, ValueSetEntry>();
    public HashMap<String, String> valueSetURIMap = new HashMap<String, String>();

    public void parseLine(String line) throws URISyntaxException, MalformedURLException {
        String[] values = line.split("\\|");
        ValueSetEntry entry = new ValueSetEntry();
        entry.vsName = values[0];
        entry.uri = new URI(values[1]);
        if (!values[2].equalsIgnoreCase("null")) {
            entry.excelURL = new URL(values[2]);
            valueSetExcelMap.put(entry.vsName, entry);
        }
        if (!values[3].equalsIgnoreCase("null")) {
            entry.page = values[3];
        }
        valueSetURIMap.put(entry.vsName, entry.uri.toString());

    }

    public class ValueSetEntry {
        public URL excelURL;
        public String page;
        public URI uri;
        public String vsName;
    }
}
