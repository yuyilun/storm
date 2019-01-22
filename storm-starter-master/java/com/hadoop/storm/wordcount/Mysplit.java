package com.hadoop.storm.wordcount;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class Mysplit implements IBasicBolt {

	private static final long serialVersionUID = 8326787924231675667L;
	String pattern = null;

	public Mysplit(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		try {
			String sen = input.getString(0);
			if (sen != null) {
				for (String word : sen.split(pattern)) {
					collector.emit(new Values(word));
				}
			}
		} catch (FailedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
