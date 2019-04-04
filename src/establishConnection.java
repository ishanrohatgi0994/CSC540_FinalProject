import java.sql.*;  
    
class establishConnection{  
    public Connection getConnection(){  
    	try{  
	    	Class.forName("com.mysql.cj.jdbc.Driver");  
	    	Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital","root","admin");  
	    	Statement stmt=con.createStatement();  
	    	stmt.executeQuery("select * from doctor");  
	    	return con;  
    	}
    	catch(Exception e)
    	{ 
    		System.out.println(e.getMessage());
    	}
		return null;  
    }  
}  