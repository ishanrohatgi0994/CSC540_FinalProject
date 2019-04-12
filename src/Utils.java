import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {
    // function for interactively reading an attribute from the console
    public static String readAttribute(String attrName, String entityName, boolean CanBeEmpty) {
    	try {
	        String displayString = "Enter " + attrName + " of the "+ entityName+". ";
	        if(CanBeEmpty) {
	            displayString = displayString + " Leave blank if empty";
	        }
	        System.out.println(displayString);
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String x = br.readLine();
	
	        // If the attribute cannot be empty, keep on asking the user until he enters a valid attribute
	        if(!CanBeEmpty && x.equals("")) {
	            while(x.equals("")) {
	                System.out.println(attrName+" cannot be empty. Please re-enter");
	                x = br.readLine();
	            }
	        }
	        return x;
    	}catch(Exception e) {
    		System.out.println("Error while reading attribute");
    	}
    	return "";
    }
}
