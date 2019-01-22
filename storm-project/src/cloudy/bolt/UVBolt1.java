package cloudy.bolt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cloudy.tools.DateFmt;

public class UVBolt1 implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Long> map = new HashMap<String, Long>();
	Set<String> hasEmittedSet = new HashSet<String>();// ��¼ͬһ�죬ͬһ��session_id���ʵļ���
	OutputCollector collector = null;
	String today = null;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		today = DateFmt.getCountDate(null, DateFmt.date_short);
	}

	@Override
	public void execute(Tuple input) {
		// handle:date,session_id
		try {

			if (input == null) {
				throw new Exception("this tuple is empty ...");
			}

			String date = input.getString(0);
			String session_id = input.getString(1);

			if (today.compareTo(date) < 0) {
				// ���촦��
				today = date;
				map.clear();
				hasEmittedSet.clear();
			}

			String key = date + "_" + session_id;
			//ͬһ�죬ͬһ�����ٴη��ʲ��ټ�¼
			if (hasEmittedSet.contains(key)) {
				throw new Exception("this tuple has emitted ...");
			}

			Long uv = map.get(key);
			if (uv == null) {
				uv = 0L;
			}
			uv++;
			map.put(key, uv);

			collector.emit(new Values(key));
			hasEmittedSet.add(key);
			collector.ack(input);// ��ʽ�ص�

		} catch (Exception e) {
		}

	}

	@Override
	public void cleanup() {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("date_SessionId"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
