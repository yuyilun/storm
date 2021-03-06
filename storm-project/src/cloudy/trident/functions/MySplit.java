package cloudy.trident.functions;


import backtype.storm.tuple.Values;
import cloudy.tools.DateFmt;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class MySplit extends BaseFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String patton = null;

	public MySplit(String patton) {
		this.patton = patton;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String log = tuple.getString(0);
		String logArr[] = log.split(patton);

		if (logArr.length == 3) {
			collector.emit(new Values(DateFmt.getCountDate(logArr[2],DateFmt.date_short),logArr[1]));
		}

	}
}