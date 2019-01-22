package com.hadoop.storm.transaction1;

import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class MyTxBolt extends BaseTransactionalBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer count = 0;
	BatchOutputCollector collector;
	TransactionAttempt id;

	@Override
	public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
		this.id = id;
		this.collector = collector;
		System.err.println("MyTxBolt prepare :" + id.getTransactionId() + " ;AttemptId :" + id.getAttemptId());
	}

	@Override
	public void execute(Tuple tuple) {
		TransactionAttempt tx = (TransactionAttempt) tuple.getValue(0);
		System.err
				.println("MyTxBolt TransactionAttempt :" + tx.getTransactionId() + " ;AttemptId :" + tx.getAttemptId());
		String log = tuple.getString(1);
		if (log != null && log.length() > 0) {
			count++;
		}

		System.err.println("log count :" + count);

	}

	@Override
	public void finishBatch() {
		collector.emit(new Values(id, count));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tx", "count"));
	}

}
