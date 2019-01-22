package cloudy.bolt;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import cloudy.hbase.dao.HBaseDao;
import cloudy.hbase.dao.imp.HBaseDaoImp;
import cloudy.tools.DataUtils;
import cloudy.tools.DateFmt;

/**
 * 全局汇总
 * 
 */
public class UVRsltBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OutputCollector collector = null;
	// 日期，非跳出UV数
	Map<String, Long> uvMap = new HashMap<String, Long>();
	long beginTime = System.currentTimeMillis();
	long endTime = 0;
	HBaseDao dao = null;

	String today = null;
	int hour = 0;
	long hour_uv = 0;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		dao = new HBaseDaoImp();
		today = DateFmt.getCountDate(null, DateFmt.date_short);
		hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ;
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {

		if (input != null) {
			String key = input.getString(0);
			String date = key.split("_")[0];

			if (today != date && today.compareTo(date) < 0) {
				uvMap.clear();// 跨天处理
			}
			
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) != hour) {
				//跨小时处理
				hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				hour_uv = 0;
			}
			
			Long uvCount = uvMap.get(date);
			if (uvCount == null) {
				uvCount = 0L;
			}

			uvCount++;
			hour_uv++;
			uvMap.put(date, uvCount);

			// 定时写库
			endTime = System.currentTimeMillis();
			if (endTime - beginTime >= 5000) {

				String rowKey = DateFmt.getCountDate(null, DateFmt.date_minute);
				String[] columns = new String[] { "time_title", "x_value", "uv" };
				String[] values = DataUtils.getValues(uvMap.get(today) + "");

				// 5s 写一次库
				// 保存历史点，为了去月环比 ，可以每分钟写一次
				dao.insert("uv_table", rowKey, "cf", columns, values);

				// 用于实时刷新
				dao.insert("uv_table", today + "_lastest", "cf", columns, values);
				
				// 小时级柱图数据
				dao.insert("uv_table", today+"_hour_"+hour, "cf", new String[]{"uv"}, new String[]{hour_uv+"" });
				
				beginTime = System.currentTimeMillis();
			}
			collector.ack(input);// 显式回调
		}

	}

	@Override
	public void cleanup() {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
