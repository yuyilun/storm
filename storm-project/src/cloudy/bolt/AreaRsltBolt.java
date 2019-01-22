package cloudy.bolt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import cloudy.hbase.dao.HBaseDao;
import cloudy.hbase.dao.imp.HBaseDaoImp;

public class AreaRsltBolt implements IBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Double> countMap = null;
	HBaseDao hBaseDao = null;

	long beginTime = System.currentTimeMillis();
	long endTime = 0L;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		countMap = new HashMap<String, Double>();
		hBaseDao = new HBaseDaoImp();
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		String date_area = input.getString(0);
		Double order_amt = input.getDouble(1);

		countMap.put(date_area, order_amt);
		endTime = System.currentTimeMillis();
		if (endTime - beginTime >= 5 * 1000) {
			for (String key : countMap.keySet()) {
				hBaseDao.insert("area_order", key, "cf", "order_amt", countMap.get(key) + "");
			}

			beginTime = System.currentTimeMillis();

		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
