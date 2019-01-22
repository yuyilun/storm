package com.hadoop.storm.transaction1;

import java.io.Serializable;

public class MyMata implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long beginPoint;
	
	private int num;//batch number

	public long getBeginPoint() {
		return beginPoint;
	}

	public void setBeginPoint(long beginPoint) {
		this.beginPoint = beginPoint;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "MyMata [beginPoint=" + beginPoint + ", num=" + num + "]";
	}
	
}
