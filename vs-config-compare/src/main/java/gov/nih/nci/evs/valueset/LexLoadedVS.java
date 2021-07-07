package gov.nih.nci.evs.valueset;

//This is the object that will hold the translated JSON from CTS2
//   It's main purpose is to extract a list of value set URIs from LexEVS
//   for use in comparison to the locally generated value_set_report_config.txt


import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class LexLoadedVS {


    /**
     * Structure of JSON coming from CTS2
     *
     * Root
     * -ValueSetCatalogEntryDirectory
     * --Entry
     * ---CurrentDefinition
     * ----ValueSetDefinition
     * ----ValueSet
     * ---ResourceSynopsis
     * --Heading
     * ---Parameter
     */

    //Map of value set codes from CTS2
    HashMap<String, String> valueSetURIMap = new HashMap<String, String>();


    public HashMap<String, String> parse(JSONObject object) {
        JSONObject obj = (JSONObject) object.get("ValueSetCatalogEntryDirectory");
        Long numEntries = (Long) obj.get("numEntries");
        JSONArray entryArray = (JSONArray) obj.get("entry");
        //Quick QA check that the entryArray is valid
        if (!(entryArray.size() == numEntries)) {
            System.out.println("Number of entries does not match array size");
        }


        for (int i = 0; i < entryArray.size(); i++) {
            JSONObject entryObject = (JSONObject) entryArray.get(i);
            Entry newEntry = new Entry();
            newEntry.parse(entryObject);

            LexValueSetEntry valueSetEntry = new LexValueSetEntry(newEntry);
            valueSetURIMap.put(valueSetEntry.vsName, valueSetEntry.uri);
        }
        return valueSetURIMap;
    }


    public class Entry {
        public CurrentDefinition currentDefinition;
        public String valueSetName;  //match field


        public void parse(JSONObject entryObject) {
            valueSetName = (String) entryObject.get("valueSetName");
            JSONObject defObject = (JSONObject) entryObject.get("currentDefinition");
            currentDefinition = new CurrentDefinition();
            currentDefinition.parse(defObject);

        }
    }

    public class CurrentDefinition {
        public ValueSetDefinition valueSetDefinition;


        public void parse(JSONObject object) {
            valueSetDefinition = new ValueSetDefinition();
            valueSetDefinition.parse((JSONObject) object.get("valueSetDefinition"));

        }
    }

    public class ValueSetDefinition {

        public String uri;  //match
        public void parse(JSONObject object) {
            uri = (String) object.get("uri");

        }
    }

    public class LexValueSetEntry {
        public String uri;
        public String vsName;

        public LexValueSetEntry(Entry entryObject) {
            vsName = entryObject.valueSetName;
            uri = entryObject.currentDefinition.valueSetDefinition.uri;
        }
    }


}
