package com.hadoop.storm.visits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class PVSumBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OutputCollector collector = null;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	Map<Long, Long> map = new HashMap<Long, Long>();

	@Override
	public void execute(Tuple input) {

		Long key = input.getLong(0);
		long value = input.getLong(1);
		map.put(key, value);
		Iterator<Long> i = map.values().iterator();
		long word_sum = 0;
		while (i.hasNext()) {
			word_sum += i.next();
		}

		System.err.println("pv_all = " + word_sum);

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("threadId", "pv"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
