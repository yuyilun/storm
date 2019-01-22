package cloudy.bolt;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cloudy.tools.DateFmt;

public class LogFmtBolt implements IBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("date", "session_id"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String log = input.getString(0);
		try {
			if (log != null) {
				String[] logArr = log.split("\t");
				// host,session_id,date -> date,session_id
				collector.emit(new Values(DateFmt.getCountDate(logArr[2], DateFmt.date_short), logArr[1]));
			}
		} catch (Exception e) {
		}

	}

}
