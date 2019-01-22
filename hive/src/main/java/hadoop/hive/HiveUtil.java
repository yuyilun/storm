package hadoop.hive;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveUtil {
	

    // 创建表  
    public static void createTable(String sql) throws SQLException {  
        Connection conn = DBHelper.getHiveConnection();  
        Statement stmt = conn.createStatement();  
        ResultSet res = stmt.executeQuery(sql);  
    }  
  
    // 依据条件查询数据  
    public static ResultSet queryData(String sql) throws SQLException {  
        Connection conn = DBHelper.getHiveConnection();  
        Statement stmt = conn.createStatement();  
        ResultSet res = stmt.executeQuery(sql);  
        return res;  
    }  
  
    // 加载数据  
    public static void loadData(String sql) throws SQLException {  
        Connection conn = DBHelper.getHiveConnection();  
        Statement stmt = conn.createStatement();  
        ResultSet res = stmt.executeQuery(sql);  
    }  
  
    // 把数据存储到 MySQL 中  
    public static void hiveToMySQL(ResultSet res) throws SQLException {  
        Connection conn = DBHelper.getMysqlConnection();  
        Statement stmt = conn.createStatement();  
        while (res.next()) {  
            String rdate = res.getString(1);  
            String time = res.getString(2);  
            String type = res.getString(3);  
            String relateclass = res.getString(4);  
            String information = res.getString(5) + res.getString(6)  
                    + res.getString(7);  
            StringBuffer sql = new StringBuffer();  
            sql.append("insert into hivelog values(0,'");  
            sql.append(rdate + "','");  
            sql.append(time + "','");  
            sql.append(type + "','");  
            sql.append(relateclass + "','");  
            sql.append(information + "')");  
  
            int i = stmt.executeUpdate(sql.toString());  
        }  
    }
	
}
