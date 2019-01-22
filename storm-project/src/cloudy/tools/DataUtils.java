package cloudy.tools;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class DataUtils {

	private static final String COLON = ":";
	private static final String UNDERLINE = "_";
	private static final String DEFAULT_COLUMNFAMILYFIELD = "cf";
	private static final String DEFAULT_COLUMNNAME = "cn";

	public static String getInputMsg(String date, String columnFamilyField, String columnName, int size) {

		if (StringUtils.isEmpty(date)) {
			date = DateFmt.getCountDate(null, DateFmt.date_short);
		}
		if (StringUtils.isEmpty(columnFamilyField)) {
			columnFamilyField = DEFAULT_COLUMNFAMILYFIELD;
		}
		if (StringUtils.isEmpty(columnName)) {
			columnName = DEFAULT_COLUMNNAME;
		}

		if (size <= 0) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= size; i++) {
			String lastValue = columnName + UNDERLINE + i;
			builder.append(date).append(COLON).append(columnFamilyField).append(COLON).append(lastValue).append(" ");
		}
		return builder.toString().trim();
	}
	
	
	public static String getInputMsg(String date, String columnFamilyField, String columnName,List<String> list) {

		if (StringUtils.isEmpty(date)) {
			date = DateFmt.getCountDate(null, DateFmt.date_short);
		}
		if (StringUtils.isEmpty(columnFamilyField)) {
			columnFamilyField = DEFAULT_COLUMNFAMILYFIELD;
		}
		if (StringUtils.isEmpty(columnName)) {
			columnName = DEFAULT_COLUMNNAME;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			String lastValue = columnName + UNDERLINE + list.get(i);
			builder.append(date).append(COLON).append(columnFamilyField).append(COLON).append(lastValue).append(" ");
		}
		return builder.toString().trim();
	}
	
	
	public static Map<String, String> getProvinceMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "����");
		map.put("2", "�Ϻ�");
		map.put("3", "����");
		map.put("4", "����");
		map.put("5", "�人");
		map.put("6", "�Ͼ�");
		map.put("7", "����");
		map.put("8", "���");
		
		return map;
	}
	
	
	public static String[] getValues(String uv) {
		
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		//������
		int curSecNum = hour * 3600 + minute * 60 + sec;
		Double xValue = (double) curSecNum / 3600;
		// ʱ�䣬����ֵ
		String[] data = { hour + ":" + minute, xValue.toString(),uv };
		return data;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
