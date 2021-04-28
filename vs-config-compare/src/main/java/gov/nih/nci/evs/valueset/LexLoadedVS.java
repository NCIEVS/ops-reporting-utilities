package gov.nih.nci.evs.valueset;

//This is the object that will hold the translated JSON from CTS2




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class LexLoadedVS {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */


    /**
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

//    List<LexValueSetEntry> valueSetEntryList = new ArrayList<LexValueSetEntry>();
    HashMap<String,String> valueSetURIMap = new HashMap<String,String>();

    public HashMap<String,String> parse(JSONObject object){
        JSONObject obj= (JSONObject) object.get("ValueSetCatalogEntryDirectory") ;

//        ValueSetCatalogEntryDirectory entryDirectory = new ValueSetCatalogEntryDirectory();
//        entryDirectory.parse(obj);

        Long numEntries = (Long) obj.get("numEntries");
        JSONArray entryArray = (JSONArray) obj.get("entry");
        if(!(entryArray.size()==numEntries)){
            System.out.println("Number of entries does not match array size");
        }

        List<LexValueSetEntry> valueSetEntries = new ArrayList<LexValueSetEntry>();
        for(int i=0;i<entryArray.size();i++){
            JSONObject entryObject = (JSONObject) entryArray.get(i);
            Entry newEntry = new Entry();
            newEntry.parse(entryObject);

            LexValueSetEntry valueSetEntry = new LexValueSetEntry(newEntry);
//            valueSetEntryList.add(valueSetEntry);
            valueSetURIMap.put(valueSetEntry.vsName,valueSetEntry.uri);
        }
        return valueSetURIMap;
    }
//
//
//    public class ValueSetCatalogEntryDirectory{
//        public List<Entry> entry;
//        public String complete;
//        public Long numEntries;
//
//        public void parse(JSONObject obj) {
//            complete = (String) obj.get("complete");
//            numEntries = (Long) obj.get("numEntries");
//
//
//            entry = new ArrayList<Entry>();
//            JSONArray entryArray = (JSONArray) obj.get("entry");
//            if(!(entryArray.size()==numEntries)){
//                System.out.println("Number of entries does not match array size");
//            }
//
//            for(int i=0;i<entryArray.size();i++){
//                JSONObject entryObject = (JSONObject) entryArray.get(i);
//                Entry newEntry = new Entry();
//                newEntry.parse(entryObject);
//                entry.add(newEntry);
//            }
//        }
//    }


    public class Entry{
        public String valueSetName;  //match field
        public CurrentDefinition currentDefinition;
//        public String about;
//        public String formalName;
//        public ResourceSynopsis resourceSynopsis;
//        public String href;
//        public String resourceName;

        public void parse(JSONObject entryObject) {
            valueSetName = (String) entryObject.get("valueSetName");
//            about = (String) entryObject.get("about");
//            formalName =(String) entryObject.get("formalName");
//            href = (String) entryObject.get("href");
//            resourceName = (String) entryObject.get("resourceName");
//
            JSONObject defObject = (JSONObject) entryObject.get("currentDefinition");
            currentDefinition = new CurrentDefinition();
            currentDefinition.parse(defObject);
//
//            JSONObject synObject = (JSONObject) entryObject.get("resourceSynopsis");
//            resourceSynopsis = new ResourceSynopsis();
//            resourceSynopsis.parse(synObject);
        }
    }

    public class CurrentDefinition{
        public ValueSetDefinition valueSetDefinition;
//        public ValueSet valueSet;

        public void parse(JSONObject object) {
            valueSetDefinition = new ValueSetDefinition();
            valueSetDefinition.parse((JSONObject)object.get("valueSetDefinition"));

//            valueSet = new ValueSet();
//            valueSet.parse((JSONObject)object.get("valueSet"));
        }
    }

    public class ValueSetDefinition{
//        public String content;
        public String uri;  //match
//        public String href;

        public void parse(JSONObject object) {
//            content = (String) object.get("content");
            uri = (String) object.get("uri");
//            href = (String) object.get("href");
        }
    }
//
//    public class ValueSet{
//        public String content;
//
//        public void parse(JSONObject object) {
//            content = (String) object.get("content");
//        }
//    }
//
//
//    public class ResourceSynopsis{
//        public String value;
//
//        public void parse(JSONObject object) {
//            value = (String) object.get("value");
//        }
//    }
//
//
public class LexValueSetEntry{
    public String vsName;
    public String uri;

    public LexValueSetEntry(Entry entryObject) {
        vsName = entryObject.valueSetName;
        uri = entryObject.currentDefinition.valueSetDefinition.uri;
    }
}


}
