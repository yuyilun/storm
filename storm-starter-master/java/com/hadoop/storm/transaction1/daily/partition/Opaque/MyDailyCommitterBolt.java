package com.hadoop.storm.transaction1.daily.partition.Opaque;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.coordination.IBatchBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Tuple;

public class MyDailyCommitterBolt implements IBatchBolt<TransactionAttempt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Map<String, DbValue> dbMap = new HashMap<String, DbValue>();
	public static final String GLOBAL_KEY = "GLOBAL_KEY";

	TransactionAttempt id;
	BatchOutputCollector collector;
	Map<String, Integer> countMap = new HashMap<String, Integer>();
	String today = null;

	public static class DbValue {
		BigInteger txid;
		int count = 0;
		String dateStr;
		int pre_count;
	}

	@Override
	public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
		this.id = id;
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		today = tuple.getString(1);
		Integer count = tuple.getInteger(2);
		id = (TransactionAttempt) tuple.getValue(0);

		if (today != null && count != null) {
			Integer batchCount = countMap.get(today);

			if (batchCount == null) {
				batchCount = 0;
			}
			batchCount += count;
			countMap.put(today, batchCount);
		}
	}

	@Override
	public void finishBatch() {
		DbValue dbValue = dbMap.get(today);
		DbValue newValue;
		if(countMap.size() > 0) {
			if (dbValue == null || !dbValue.txid.equals(id.getTransactionId())) {
				newValue = new DbValue();
				newValue.txid = id.getTransactionId();
				newValue.dateStr = today;
				if (dbValue == null) {
					newValue.count = countMap.get(today);
					newValue.pre_count = 0;
				} else {
					newValue.pre_count = dbValue.count;
					newValue.count = dbValue.count + countMap.get(today);
				}
				dbMap.put(today, newValue);
			} else {
				newValue = dbValue;
			}
			System.err.println("total ============================== :" + dbMap.get(today).count);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
