package cloudy.trident.tools;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class SplitBy extends BaseFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String patten = null;

	public SplitBy(String patten) {
		this.patten = patten;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		if (!tuple.isEmpty()) {
			String msg = tuple.getString(0);
			String[] values = msg.split(patten);
			collector.emit(new Values(values[0],values[1],values[2]));
		}
	}

}
