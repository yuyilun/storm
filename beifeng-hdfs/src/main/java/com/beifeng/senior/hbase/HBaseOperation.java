package com.beifeng.senior.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;

/**
 * CRUD Operations
 * 
 * 
 * @author yu100
 *
 */
public class HBaseOperation {

	public static HTable getHtableByTableName(String tableName) throws Exception {
		// Get instance of default Configuration
		Configuration configuration = HBaseConfiguration.create();

		// get table instance
		HTable hTable = new HTable(configuration, tableName);

		return hTable;
	}
	
	public static void getData(String tableName)  throws Exception {
		HTable hTable = getHtableByTableName(tableName);

		// create get with rowkey
		Get get = new Get(Bytes.toBytes("10002"));
		
		
		//add column
		get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
		get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));

		// get data
		Result result = hTable.get(get);

		//value	
		for (Cell cell : result.rawCells()) {

			System.out.println(
					Bytes.toString(CellUtil.cloneFamily(cell)) + ":" 
			+ Bytes.toString(CellUtil.cloneQualifier(cell))
			+ " -> " + Bytes.toString(CellUtil.cloneValue(cell)));
		}
		
		// table close
		hTable.close();
	}
	
	public static void putData()  throws Exception {
		String tableName = "user"; // default.user / hbase:meta
		HTable hTable = getHtableByTableName(tableName);
		
		Put put = new Put(Bytes.toBytes("10004"));
		
		// add a column with value
		put.add(
				Bytes.toBytes("info"),
				Bytes.toBytes("name"),
				Bytes.toBytes("zhaoliu"));
		
		put.add(
				Bytes.toBytes("info"),
				Bytes.toBytes("age"),
				Bytes.toBytes(25));
		
		put.add(
				Bytes.toBytes("info"),
				Bytes.toBytes("address"),
				Bytes.toBytes("shanghai"));
		
		hTable.put(put);
		
		// table close
		hTable.close();
	}
	
	
	public static void deleteData() throws Exception {
		String tableName = "user"; // default.user / hbase:meta
		HTable hTable = getHtableByTableName(tableName);
		
		Delete delete = new Delete(Bytes.toBytes("10004"));
		
		//delete.deleteColumn(Bytes.toBytes("info"),
		//		Bytes.toBytes("address"));
		delete.deleteFamily(Bytes.toBytes("info"));
		
		hTable.delete(delete);
		
		hTable.close();
	}

	public static void main(String[] args) throws Exception { 
		String tableName = "user"; // default.user / hbase:meta 
		HTable hTable = null; 
		ResultScanner resultScanner = null; 
		try { 
			hTable = getHtableByTableName(tableName); 
			
			Scan scan = new Scan(); 
			
			//range :[start,stop) 
			scan.setStartRow(Bytes.toBytes("10002")); 
			scan.setStopRow(Bytes.toBytes("10003")); 
			
			//
			//scan.addColumn(family, qualifier);
			//scan.addFamily(family);
			//Scan scan = new Scan(startRow, stopRow);
			
			//PrefixFilter
			//PageFilter
			//scan.setFilter(filter);
			
			//
			//scan.setCacheBlocks(cacheBlocks);
			//scan.setCaching(caching);
			
			resultScanner = hTable.getScanner(scan); 
			
			for(Result result : resultScanner) { 
				
				System.out.println(Bytes.toString(result.getRow())); 
				//System.out.println(result); 
				for (Cell cell : result.rawCells()) { 

					System.out.println( 
							Bytes.toString(CellUtil.cloneFamily(cell)) + ":" 
					+ Bytes.toString(CellUtil.cloneQualifier(cell)) 
					+ " -> " + Bytes.toString(CellUtil.cloneValue(cell))); 
				} 
				System.out.println("------------------------------------"); 
			} 
		}catch (Exception e) { 
			e.printStackTrace(); 
		}finally { 
			IOUtils.closeStream(resultScanner); 
			IOUtils.closeStream(hTable); 
		} 
	} 
} 