package cloudy.bolt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cloudy.hbase.dao.HBaseDao;
import cloudy.hbase.dao.imp.HBaseDaoImp;
import cloudy.tools.DateFmt;

public class AreaAmtBolt implements IBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Double> countMap = null;
	HBaseDao hBaseDao = null;
	String today = null;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("date_area", "amt"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		hBaseDao = new HBaseDaoImp();

		today = DateFmt.getCountDate(null, DateFmt.date_short);

		countMap = initMap(today, hBaseDao);

		for (String key : countMap.keySet()) {

			System.out.println("key: " + key + " ;value: " + countMap.get(key));

		}

	}

	public Map<String, Double> initMap(String rowKeyDate, HBaseDao hBaseDao) {

		Map<String, Double> map = new HashMap<String, Double>();

		List<Result> rows = hBaseDao.getRows("area_order", rowKeyDate, new String[] { "order_amt" });

		for (Result rs : rows) {
			String rowKey = Bytes.toString(rs.getRow());
			for (KeyValue kv : rs.raw()) {
				if ("order_amt".equals(Bytes.toString(kv.getQualifier()))) {
					map.put(rowKey, Double.parseDouble(Bytes.toString(kv.getValue())));
					break;
				}
			}
		}
		return map;
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// area_id , count
		int area_id = Integer.parseInt(input.getString(0));
		double order_amt = Double.parseDouble(input.getString(1));
		
		String order_date = input.getStringByField("order_date") ;
		if(!order_date.equals(today)) {
			countMap.clear();
		}

		Double count = countMap.get(order_date + "_" + area_id);

		if (count == null) {
			count = 0.0;
		}

		count += order_amt;
		countMap.put(order_date + "_" + area_id, count);

		collector.emit(new Values(order_date + "_" + area_id, count));
	}

	@Override
	public void cleanup() {
		countMap.clear();
	}

}
