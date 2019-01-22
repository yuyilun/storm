package com.hadoop.storm.visits;

import java.net.InetAddress;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class PVBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ZK_PATH = "/lock/storm/pv";
	ZooKeeper zooKeeper = null;
	String lockData = null;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// this.collector = collector;
		try {
			zooKeeper = new ZooKeeper("192.168.60.131:2181,192.168.60.132:2181", 3000, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					System.out.println("event: " + event.getType());
				}
			});

			while (zooKeeper.getState() != ZooKeeper.States.CONNECTED) {
				Thread.sleep(1000);
			}

			InetAddress address = InetAddress.getLocalHost();
			lockData = address.getHostAddress() + ":" + context.getThisTaskId();

			if (zooKeeper.exists(ZK_PATH, false) == null) {
				zooKeeper.create(ZK_PATH, lockData.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}

		} catch (Exception e) {
			try {
				zooKeeper.close();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	String logString = null;
	String session_id = null;
	long Pv = 0;
	long beginTime = System.currentTimeMillis();
	long endTime = 0;

	@Override
	public void execute(Tuple input) {

		try {
			endTime = System.currentTimeMillis();
			logString = input.getString(0);

			if (logString != null) {
				session_id = logString.split("\t")[1];
				if (session_id != null) {
					Pv++;
				}
			}
			if (endTime - beginTime >= 5 * 1000) {
				if (lockData.equals(new String(zooKeeper.getData(ZK_PATH, false, null)))) {
					System.err.println("#################### pv= " + Pv * 4);
				}
				beginTime = System.currentTimeMillis();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		try {
			zooKeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
