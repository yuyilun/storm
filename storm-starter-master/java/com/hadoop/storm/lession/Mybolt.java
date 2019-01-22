package com.hadoop.storm.lession;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class Mybolt implements IRichBolt {
	
	OutputCollector collector = null ;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector = collector;
	}

	int num = 0;
	String value = null;

	@Override
	public void execute(Tuple input) {
		// step 1 : get data from spout
		try {
			value = (String) input.getValueByField("log");
			if (value != null) {
				num++;
				
				System.err.println(Thread.currentThread().getName() + " lines  :" + num + "   session_id:" + value.split("\t")[1]);
			}
			//step 1.1 : if result access, call back ack
			collector.ack(input);
			
			Thread.sleep(200);
			
		} catch (Exception e) {
			//step 1.2 : if result fail, call back fail
			collector.fail(input);
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// step 2 : declare output field
		declarer.declare(new Fields(""));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
