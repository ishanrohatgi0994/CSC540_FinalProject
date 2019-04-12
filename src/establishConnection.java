import java.sql.*;  
    
class establishConnection{  
    public Connection getConnection(){  
    	// Establishes connection to our locally installed DB instance.
    	// Returns the connection object
    	try{  
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	//Connection con=DriverManager.getConnection("jdbc:mysql://10.154.59.5:3306/hospital?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","admin");
	    	Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital","root","admin");
	    	Statement stmt=con.createStatement();
	    	return con;  
    	}
    	catch(Exception e)
    	{
    		//System.out.println(e.getMessage());
    		System.out.println("Error in establishing connection");
    	}
		return null;  
    }  
}  