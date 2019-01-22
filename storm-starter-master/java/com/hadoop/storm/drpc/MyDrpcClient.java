package com.hadoop.storm.drpc;

import org.apache.thrift7.TException;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;

public class MyDrpcClient {
	
	public static void main(String[] args) {
		DRPCClient client = new DRPCClient("192.168.60.131", 3772);
		try {
			String result = client.execute("exclamation", "test");
			System.out.println(result);
		} catch (TException e) {
			e.printStackTrace();
		} catch (DRPCExecutionException e) {
			e.printStackTrace();
		} 
	}
	
}
