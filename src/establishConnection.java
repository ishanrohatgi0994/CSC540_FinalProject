import java.sql.*;  
    
class establishConnection{  
    public Connection getConnection(){  
    	try{  
	    	Class.forName("com.mysql.cj.jdbc.Driver");  
	    	Connection con=DriverManager.getConnection("jdbc:mysql://192.168.0.113:3306/hospital?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","admin");
	    	Statement stmt=con.createStatement();  
	    	//ResultSet rs=stmt.executeQuery("select * from doctor");
	    	//while(rs.next())  
	    		//System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
	    	return con;  
    	}
    	catch(Exception e)
    	{ 
    		e.printStackTrace();
    	}
		return null;  
    }  
}  