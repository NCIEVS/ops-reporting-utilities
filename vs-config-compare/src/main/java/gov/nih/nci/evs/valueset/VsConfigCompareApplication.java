package gov.nih.nci.evs.valueset;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This application reads in a valueset-report-config.txt file
 *    and a CTS2 valueset query and compares the two lists
 *    for mismatches
 *
 *    https://lexevs65cts2.nci.nih.gov/lexevscts2/valuesets?maxtoreturn=2000&format=json
 */



public class VsConfigCompareApplication {
    String configFilePath="./valueset-report-config.txt";
    String lexevsLocation="lexevscts2";

    String lexevsURL = "https://lexevs65cts2.nci.nih.gov/lexevscts2/valuesets?maxtoreturn=2000&format=json";
    ConfigLoadedVS configLoadedVS = new ConfigLoadedVS();
    LexLoadedVS lexLoadedVS = new LexLoadedVS();
    boolean doFTP = false;
    boolean doCTS2 = false;

    TreeSet<String> errorMessages = new TreeSet<String>();
    TreeSet<String> noMatchLex = new TreeSet<String>();
    TreeSet<String> noMatchLocal = new TreeSet<String>();
    TreeSet<String> badLinks = new TreeSet<String>();

    public static void main(String[] args) {
        //The two args should be valueset-report-config.txt path
        //  and CTS2 query URL
        printHelp();
        VsConfigCompareApplication application = new VsConfigCompareApplication();
        application.config(args);
        if(application.load()) {
            if(application.doCTS2) {
                application.compare();
            }
            if (application.doFTP) {
                application.doFTP();
            }
        }
        application.printReport();

    }


    public static void printHelp(){
        System.out.println("This program needs two parameters");
        System.out.println(" -V -- is path to valueset-report-config.txt (Default: current directory)");
        System.out.println(" -L -- is location of cts2 service (Default: lexevscts2). Only needed if doing CTS2 comparison");
        System.out.println(" -F -- include comparison to FTP");
        System.out.println(" -C -- include comparison to CTS2");
    }

    public void config(String[] args){
        if(args.length>0){
            for (int i = 0; i < args.length; i++) {
                String option = args[i];
                if (option.equalsIgnoreCase("--help")) {
                    this.printHelp();
                    System.exit(0);
                }
                else if(option.equalsIgnoreCase("-V")){
                    this.configFilePath=args[++i];
                }
                else if(option.equalsIgnoreCase("-L")){
                    this.lexevsLocation=args[++i];
                    String lexevsQuery = ".nci.nih.gov/lexevscts2/valuesets?maxtoreturn=2000&format=json";
                    this.lexevsURL="https://"+ this.lexevsLocation+lexevsQuery;
                } else if (option.equalsIgnoreCase("-F")){
                    doFTP = true;
                } else if (option.equalsIgnoreCase("-C")){
                    doCTS2 = true;
                }
                else {
                    System.out.println(option +" is not a valid option");
                    printHelp();
                    System.exit(1);
                }
            }
        }
    }

    private boolean load(){
        boolean loadSuccessful = true;

        //Load config file
        loadSuccessful = loadConfig();
        if(!loadSuccessful) return false;

        //load value set list from cts2
        if(doCTS2) {
            loadSuccessful = loadCTS2();
            if (!loadSuccessful) return false;
        }
        return loadSuccessful;
    }



    private boolean loadConfig() {
        boolean loadSuccessful = true;
        String line="";
        try {

            File file = new File(this.configFilePath);
            FileInputStream stream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            while((line=bufferedReader.readLine())!=null){
                configLoadedVS.parseLine(line);
            }
            stream.close();

        } catch (java.io.FileNotFoundException fnf){
            System.out.println("Config file not found at "+ this.configFilePath + ". Please check the path") ;
            loadSuccessful = false;
        }
        catch (IOException e) {
            System.out.println("Problem reading config file at "+ this.configFilePath);
            loadSuccessful=false;
        }
        catch (URISyntaxException e) {
            System.out.println("Unable to parse URI for value set at "+ line);
            loadSuccessful = false;
        }

        return loadSuccessful;
    }

    private boolean loadCTS2() {
        boolean loadSuccessful = true;

        try {
            URL url = new URL(this.lexevsURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode !=200){
                System.out.println("Unable to connect to LexEVS. Response code: "+ responseCode);
                return false;
            }

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());
            while(scanner.hasNext()){
                inline+= scanner.nextLine();
            }

            scanner.close();

            JSONParser parser = new JSONParser();
            JSONObject data_obj = (JSONObject) parser.parse(inline);
            lexLoadedVS.parse(data_obj);

        }
        catch (MalformedURLException e) {
            System.out.println("URL is malformed "+ this.lexevsURL);
            return false;
        }
        catch (IOException e) {
            System.out.println("Unable to read from LexEVS");
            return false;
        }
        catch (ParseException e) {
            System.out.println("Unable to parse JSON");
            return false;
        }

        return loadSuccessful;
    }

    private void compare(){
        //take the loaded value set config and cts2 results and make sure they are consistent
        HashMap<String,String> localVSMap = configLoadedVS.valueSetURIMap;
        HashMap<String,String> lexEVSMap = lexLoadedVS.valueSetURIMap;

        for(String name: localVSMap.keySet()){
            if(lexEVSMap.get(name)==null){
                noMatchLex.add("No match in LexEVS for "+ name);
            }
            else if (!(localVSMap.get(name).equals(lexEVSMap.get(name)))){
                errorMessages.add("Poor match for "+ name +". local:"+localVSMap.get(name)+ " Lex:"+lexEVSMap.get(name) );
            }
        }

        for(String name:lexEVSMap.keySet()){
            if(localVSMap.get(name)==null){
                noMatchLocal.add("LexEVS entry missing in local file "+ name );
            }
        }
        if(errorMessages.size()>0){
            System.out.println("Failed comparison");
            System.out.println("------------------");
            for(String message:errorMessages){
                System.out.println(message);
            }
            System.out.println("------------------");
            for(String message:noMatchLex){
                System.out.println(message);
            }
            System.out.println("------------------");
            for(String message:noMatchLocal){
                System.out.println(message);
            }
        }
    }


    private void doFTP() {
        //Loop through value set list and pick out any URL's that don't work
        System.out.println("------------------");
        for(String name: configLoadedVS.valueSetExcelMap.keySet()){
            URL testURL = configLoadedVS.valueSetExcelMap.get(name);

            if(!isValidLink(testURL)){
                String invalidLink = "URL is invalid for: "+ name + " URL:"+ testURL;
                badLinks.add(invalidLink);
                System.out.println("URL is invalid for: "+ name + " URL:"+ testURL);
            }


        }
    }



    public boolean isValidLink(URL testURL) {
        try {
            URLConnection connection = testURL.openConnection();
//            InputStream inputStream = connection.getInputStream();
            Long length = connection.getContentLengthLong();
            if(length<0){
                return false;
            }
            return true;

        }
        catch (Exception e) {
            return false;
        }
    }

    private void printReport(){
//        Set<String> errorMessages
//        Set<String> noMatchLex
//        Set<String> noMatchLocal
//        TreeSet<String> badLinks
        try {
            PrintWriter writer = new PrintWriter("./ValueSetQA.txt");

            writer.println("Value Set Config QA Report");
            writer.println("");
            if (errorMessages.size() > 0) {
                writer.println("Poor matches for Value Set URI");
                for(String message:errorMessages){
                    writer.println(message);
                }
                writer.println("");
                writer.println("______________________________");
            }


            if (noMatchLex.size() > 0) {
                writer.println("Value sets in config file with no match in LexEVS");
                for(String message:noMatchLex){
                    writer.println(message);
                }
                writer.println("");
                writer.println("______________________________");
            }



            if (noMatchLocal.size() > 0) {
                writer.println("Value sets in LexEVS with no match in config file");
                for(String message: noMatchLocal){
                    writer.println(message);
                }
                writer.println("");
                writer.println("______________________________");
            }



            if (badLinks.size() > 0) {
                writer.println("Value sets in config file with bad Excel file links");
                for(String message:badLinks){
                    writer.println(message);
                }
            }
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error opening file for export") ;
            e.printStackTrace();
        }

    }

}
