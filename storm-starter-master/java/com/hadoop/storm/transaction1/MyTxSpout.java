package com.hadoop.storm.transaction1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.tuple.Fields;

public class MyTxSpout implements ITransactionalSpout<MyMata> {

	Map<Long, String> map = null;

	public MyTxSpout() {
		Random random = new Random();
		map = new HashMap<Long, String>();

		String[] hosts = { "www.taobao.com" };
		String[] session_id = { "ABYH6Y4V4SCVXTG6DPB4VH9U123", "XXYH6YCGFJYERTT834R52FDXV9U34",
				"BBYH61456FGHHJ7JL89RG5VV9UYU7", "CYYH6Y2345GHI899OFG4V9U567", "VVVYH6Y4V4SFXZ56JIPDPB4V678" };
		String[] time = { "2014-01-07 08:40:50", "2014-01-07 08:40:51", "2014-01-07 08:40:52", "2014-01-07 08:40:53",
				"2014-01-07 09:40:49", "2014-01-07 10:40:49", "2014-01-07 11:40:49", "2014-01-07 12:40:49" };

		StringBuffer sbBuffer = new StringBuffer();
		for (long i = 0; i < 100; i++) {
			map.put(i, hosts[0] + "\t" + session_id[random.nextInt(5)] + "\t" + time[random.nextInt(8)]);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tx", "log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinator<MyMata> getCoordinator(Map conf, TopologyContext context) {
		return new MyCoordinator();
	}

	@Override
	public Emitter<MyMata> getEmitter(Map conf, TopologyContext context) {
		return new MyEmitter(map);
	}

}
