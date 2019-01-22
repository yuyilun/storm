package com.hadoop.storm.transaction1;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.ICommitter;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Tuple;

public class MyCommitter extends BaseTransactionalBolt implements ICommitter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Map<String, DbValue> dbMap = new HashMap<String, DbValue>();
	public static final String GLOBAL_KEY = "GLOBAL_KEY";

	int sum = 0;
	TransactionAttempt id;
	BatchOutputCollector collector;

	class DbValue {
		BigInteger txid;
		int count = 0;

	}

	@Override
	public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
		this.id = id;
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		sum += tuple.getInteger(1);
	}

	@Override
	public void finishBatch() {
		DbValue dbValue = dbMap.get(GLOBAL_KEY);
		DbValue newValue;
		if (dbValue == null || !dbValue.txid.equals(id.getTransactionId())) {
			newValue = new DbValue();
			newValue.txid = id.getTransactionId();
			if (dbValue == null) {
				newValue.count = sum;
			} else {
				newValue.count = dbValue.count + sum;
			}
			dbMap.put(GLOBAL_KEY, newValue);
		} else {	
			newValue = dbValue;
		}
		System.err.println("total ============================== :" + dbMap.get(GLOBAL_KEY).count);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
