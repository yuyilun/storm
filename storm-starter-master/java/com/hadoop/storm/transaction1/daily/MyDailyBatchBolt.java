package com.hadoop.storm.transaction1.daily;

import java.util.HashMap;
import java.util.Map;

import com.hadoop.storm.tool.DateFmt;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.coordination.IBatchBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class MyDailyBatchBolt implements IBatchBolt<TransactionAttempt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer count = null;
	String today = null;
	Map<String, Integer> countMap = new HashMap<String, Integer>();
	BatchOutputCollector collector;
	TransactionAttempt id;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tx", "today", "count"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		id = (TransactionAttempt) tuple.getValue(0);
		String log = tuple.getString(1);

		if (log != null && log.split("\t").length >= 3) {
			today = DateFmt.getCountDate(log.split("\t")[2], DateFmt.date_short);

			if (count == null) {
				count = 0;
			}
			count++;
			countMap.put(today, count);
		}
	}

	@Override
	public void finishBatch() {
		collector.emit(new Values(id, today, count));
	}

}
