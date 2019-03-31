import java.net.ConnectException;
import java.sql.*;

public abstract class establishConnection implements Connection {

    public Connection establishConnection(){

        final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/$USER$";
        Connection conn = null;

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            String user = "$USER$";
            String passwd = "$PASSWORD$";

            conn = DriverManager.getConnection(jdbcURL, user, passwd);

            return conn;
        }
        catch (Exception e){
            System.out.println("Connection not established");
            return conn;
        }
    }
}
