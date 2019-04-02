import java.sql.*;  
    
class establishConnection{  
    public Connection getConnection(){  
    	try{  
	    	Class.forName("com.mysql.cj.jdbc.Driver");  
	    	Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital","root","admin");  
	    	Statement stmt=con.createStatement();  
	    	ResultSet rs=stmt.executeQuery("select * from doctor");  
	    	//while(rs.next())  
	    		//System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
	    	return con;  
    	}
    	catch(Exception e)
    	{ 
    		System.out.println(e);
    	}
		return null;  
    }  
}  