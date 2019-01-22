package com.hadoop.storm.wordcount;

import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class MyRandomSentenceSpout extends BaseRichSpout {
	SpoutOutputCollector _collector;
	Random _rand;

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		_collector = collector;
		_rand = new Random();
	}
	
	String[] sentences = new String[] { "a b c d", "b c d e f", "a d e f" };

	@Override
	public void nextTuple() {
		
		//String sentence = sentences[_rand.nextInt(sentences.length)];
	
		for(String sentence : sentences) {
			_collector.emit(new Values(sentence));
		}
		
		Utils.sleep(10 * 1000);
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

}