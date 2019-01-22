package cloudy.trident.tools;

import backtype.storm.tuple.Values;
import cloudy.tools.DateFmt;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class OrderNumSplit extends BaseFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String patten = null;

	public OrderNumSplit(String patten) {
		this.patten = patten;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {

		if (!tuple.isEmpty()) {
			String msg = tuple.getString(0);
			String[] values = msg.split(patten);
			collector.emit(new Values(values[0], 
					Double.parseDouble(values[1]), 
					DateFmt.getCountDate(values[2], DateFmt.date_short), 
					"ordernum_"+values[3],
					"cf"));//增加columnfamily,固定值
		}

	}

}
