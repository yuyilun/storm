package hadoop.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
	
	
	private static Connection connToHive = null;
	private static Connection connToMysql = null;
	
	
	
	public static Connection getHiveConnection() throws SQLException {
		if(connToHive == null) {
			try {
				Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			connToHive = DriverManager.getConnection(
					"jdbc:hive://10.10.11.11:9083/default",
					"hadoop",
					"hadoop");
		}
		return connToHive;
	}
	
	public static Connection getMysqlConnection() throws SQLException {
		if (connToMysql == null) {  
            try {  
                Class.forName("com.mysql.jdbc.Driver");  
            } catch (ClassNotFoundException err) {  
                err.printStackTrace();  
                System.exit(1);  
            }  
            // mysql安装IP地址  
            connToMysql = DriverManager.getConnection( 
                            "jdbc:mysql://10.10.11.11:3306/hive?useUnicode=true&characterEncoding=UTF8",  
                            "hadoop", 
                            "hadoop"); // 编码不要写成UTF-8  
        }  
        return connToMysql;
	}
	
	
	 public static void closeHiveConn() throws SQLException {  
        if (connToHive != null) {  
            connToHive.close();  
        }  
    }  
  
    public static void closeMySQLConn() throws SQLException {  
        if (connToMysql != null) {  
        	connToMysql.close();  
        }  
    }  
  
    public static void main(String[] args) throws SQLException {  
        System.out.println(getMysqlConnection());  
        closeMySQLConn();  
    }  
	
	
}
