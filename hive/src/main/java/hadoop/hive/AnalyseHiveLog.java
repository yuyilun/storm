package hadoop.hive;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyseHiveLog {
	
	 public static void main(String[] args) throws SQLException {  
	        StringBuffer sql = new StringBuffer();  
	  
	        // 第一步:在 Hive 中创建表  
	        sql.append("create table if not exists loginfo( ");  
	        sql.append("rdate string,  ");  
	        sql.append("time array<string>, ");  
	        sql.append("type string, ");  
	        sql.append("relateclass string, ");  
	        sql.append("information1 string, ");  
	        sql.append("information2 string, ");  
	        sql.append("information3 string)  ");  
	        sql.append("row format delimited fields terminated by ' '  ");  
	        sql.append("collection items terminated by ','   ");  
	        sql.append("map keys terminated by  ':'");  
	  
	        System.out.println(sql);  
	        HiveUtil.createTable(sql.toString());  
	  
	        // 第二步:加载 hive 日志文件  
	        sql.delete(0, sql.length());  
	        sql.append("load data local inpath ");  
	        sql.append("'/user/hadoop/input/hivelog'");  
	        sql.append(" overwrite into table loginfo");  
	        System.out.println(sql);  
	        HiveUtil.loadData(sql.toString());  
	  
	        // 第三步:查询有用信息  
	        sql.delete(0, sql.length());  
	        sql.append("select rdate,time[0],type,relateclass,");  
	        sql.append("information1,information2,information3 ");  
	        sql.append("from loginfo where type='INFO'");  
	        System.out.println(sql);  
	        ResultSet res = HiveUtil.queryData(sql.toString());  
	  
	        // 第四步:查出的信息经过变换后保存到 MySQL 中  
	        HiveUtil.hiveToMySQL(res);  
	  
	        // 第五步:关闭 Hive 连接  
	        DBHelper.closeHiveConn();  
	  
	        // 第六步:关闭 MySQL 连接  
	        DBHelper.closeMySQLConn();  
	    }  
	
	
}
