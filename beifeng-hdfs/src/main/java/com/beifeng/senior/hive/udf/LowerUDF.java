package com.beifeng.senior.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  1. Implement one or more methods named
 * "evaluate" which will be called by Hive.
 * 
 * 2."evaluate" should never be a void method. 
 * However it can return "null" if needed.
 * 
 * @author yu100
 *
 */
public class LowerUDF extends UDF{
	
	 public Text evaluate(Text str) {
		 //validate
		 if( null == str.toString() ) {
			 return null;
		 }
		 return new Text(str.toString().toLowerCase());
	 }
	
	 public static void main(String[] args) {
		System.out.println(new LowerUDF().evaluate(new Text("HIVE")));
	}
	
}
