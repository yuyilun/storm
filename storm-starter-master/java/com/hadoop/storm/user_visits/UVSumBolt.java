package com.hadoop.storm.user_visits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hadoop.storm.tool.DateFmt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class UVSumBolt implements IBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counts = new HashMap<String, Integer>();
	String cur_Date = null;
	long beginTime = System.currentTimeMillis();
	long endTime = 0;

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
		cur_Date = DateFmt.getCountDate("2014-01-07", DateFmt.date_short);
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		try {

			endTime = System.currentTimeMillis();

			long PV = 0;
			long UV = 0;

			String date_session_id = input.getString(0);
			Integer count = input.getInteger(1);

			if (!date_session_id.startsWith(cur_Date)
					&& DateFmt.parseDate(date_session_id.split("\t")[0]).after(DateFmt.parseDate(cur_Date))) {

				cur_Date = date_session_id.split("\t")[0];
				counts.clear();

			}

			counts.put(date_session_id, count);
			Iterator<String> keys = counts.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key != null) {
					if (key.startsWith(cur_Date)) {
						UV++;
						PV += counts.get(key);
					}
				}
			}
			if (endTime - beginTime >= 5 * 1000) {
				System.err.println("PV : " + PV + " ; UV :" + UV);
			}
		} catch (Exception e) {
			throw new FailedException("split fail");
		}

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
