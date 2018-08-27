package gov.nih.nci.evs.wusage.parse;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTempTest {
	
	private static Console console = System.console();
	
	public static void main(String[] args){

		Boolean replace = false;
		if (args.length>0){
			replace = true;
		}
		String inPattern = readLine("Enter your regex: ");
		String inMatch = readLine("Enter input string to search: ");
		if (replace){
			String inReplace = readLine("Enter replace text: ");
			matcherReplace(inPattern, inMatch, inReplace);
		}else {
		matcherFind(inPattern, inMatch);}

	}
	
	private static void matcherFind(String inPattern, String inMatch){
        Pattern pattern = 
        Pattern.compile(inPattern);

        Matcher matcher = 
        pattern.matcher(inMatch);
		
		boolean found = false;
        while (matcher.find()) {
        	outLine(matcher.group(),matcher.start(),matcher.end());

            found = true;
        }
        if(!found){
            noMatch();
        }
	}
	
	private static void matcherReplace(String inPattern,String inMatch,String inReplace){
//		   String REGEX = "dog";
//	        String INPUT =
//	            "The dog says meow. All dogs say meow.";
//	        String REPLACE = "cat";
	     

	            Pattern p = Pattern.compile(inPattern);
	            // get a matcher object
	            Matcher m = p.matcher(inMatch);
	            inMatch = m.replaceAll(inReplace);
	            System.out.println(inMatch);
	}
	
    private static String readLine(String prompt) {
        String line = null;
//        Console c = System.console();
        if (console != null) {
             line = console.readLine(prompt);
        } else {
            System.out.print(prompt);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                 line = bufferedReader.readLine();
            } catch (IOException e) { 
                //Ignore    
            }
        }
        return line;
    }
    
    private static void outLine(String group, int start, int end){
    	if (console != null){
            console.format("I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n",
                    group,
                    start,
                    end);
                
    	}
    	else{
    		System.out.println("I found the text " + group +
    				" starting at "+ start +
    				" and ending at " + end);
    	
    	}
    	
    }
    
    private static void noMatch(){
    	if (console != null){
    		console.format("No match found.%n");
    	}
    	else {
    		System.out.println("No match found.");
    	}
    }
    
  
}
