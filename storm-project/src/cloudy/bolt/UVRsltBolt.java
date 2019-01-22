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
 * ȫ�ֻ���
 * 
 */
public class UVRsltBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OutputCollector collector = null;
	// ���ڣ�������UV��
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
				uvMap.clear();// ���촦��
			}
			
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) != hour) {
				//��Сʱ����
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

			// ��ʱд��
			endTime = System.currentTimeMillis();
			if (endTime - beginTime >= 5000) {

				String rowKey = DateFmt.getCountDate(null, DateFmt.date_minute);
				String[] columns = new String[] { "time_title", "x_value", "uv" };
				String[] values = DataUtils.getValues(uvMap.get(today) + "");

				// 5s дһ�ο�
				// ������ʷ�㣬Ϊ��ȥ�»��� ������ÿ����дһ��
				dao.insert("uv_table", rowKey, "cf", columns, values);

				// ����ʵʱˢ��
				dao.insert("uv_table", today + "_lastest", "cf", columns, values);
				
				// Сʱ����ͼ����
				dao.insert("uv_table", today+"_hour_"+hour, "cf", new String[]{"uv"}, new String[]{hour_uv+"" });
				
				beginTime = System.currentTimeMillis();
			}
			collector.ack(input);// ��ʽ�ص�
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
