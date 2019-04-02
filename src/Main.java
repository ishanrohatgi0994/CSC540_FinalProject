import java.sql.*;

public class Main {

    public static void main(String args[]){

        //System.out.println("Establish DB connection here");
        establishConnection ec = new establishConnection(); 
        Connection conn = ec.getConnection();
        
        if(conn!=null) {
        	//System.out.println("Write switch case here");
        	Menu menu = new Menu();
        	menu.displayOptions(conn);
        }

    }
}
