package com.hadoop.storm.lession;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * When writing topologies using Java, {@link IRichBolt} and {@link IRichSpout}
 * are the main interfaces to use to implement components of the topology
 * 
 * by 3 step , get data from file source
 * 
 * @author yu100
 *
 */
public class Myspout implements IRichSpout {
	private static final long serialVersionUID = 1L;

	FileInputStream fis;
	InputStreamReader isr;
	BufferedReader br;
	SpoutOutputCollector collector = null;

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		// step 1 : open file
		try {
			this.collector = collector;
			this.fis = new FileInputStream("track.log");
			this.isr = new InputStreamReader(fis, "UTF-8");
			this.br = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedException("split fail");
		}
	}

	@Override
	public void close() {
		//
		try {
			fis.close();
			isr.close();
			br.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	String str = null;

	@Override
	public void nextTuple() {
		// step 2 : get Tuple from file
		try {
			while ((str = this.br.readLine()) != null) {
				this.collector.emit(new Values(str));

				Thread.sleep(200);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void ack(Object msgId) {
		// ack success answer
		System.out.println("spout ack: " + msgId.toString());
	}

	@Override
	public void fail(Object msgId) {
		// ack fail answer
		System.out.println("spout fail: " + msgId.toString());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// step 3 : declare output field
		declarer.declare(new Fields("log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {

		// TODO Auto-generated method stub
		return null;
	}

}
