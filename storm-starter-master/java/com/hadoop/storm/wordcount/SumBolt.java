package com.hadoop.storm.wordcount;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class SumBolt implements IBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counts = new HashMap<String, Integer>();

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		try {
			String word = input.getString(0);
			Integer count = input.getInteger(1);
			counts.put(word, count);
			
			long word_sum = 0;
			long word_count = 0;
			
			Iterator<Integer> values = counts.values().iterator();
			while(values.hasNext()) {
				word_sum += values.next();
			}
			
			Iterator<String> keys = counts.keySet().iterator();
			while(keys.hasNext()) {
				String oneWordString = keys.next();
				if(oneWordString != null) {
					word_count ++;
				}
			}
			System.err.println("word_sum : " + word_sum + " ; word_count :" + word_count);
		} catch (Exception e) {
			throw new FailedException("split fail");
		}

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
