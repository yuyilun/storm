package com.hadoop.storm.hbase.state;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import backtype.storm.tuple.Values;
import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.state.StateType;
import storm.trident.state.map.CachedMap;
import storm.trident.state.map.MapState;
import storm.trident.state.map.NonTransactionalMap;
import storm.trident.state.map.OpaqueMap;
import storm.trident.state.map.SnapshottableMap;
import storm.trident.state.map.TransactionalMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HBaseAggregateFactory implements StateFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StateType type;
	private TridentConfig config;

	public HBaseAggregateFactory(TridentConfig config, StateType type) {
		this.type = type;
		this.config = config;

		if (config.getStateSerializer() == null) {
			config.setStateSerializer(TridentConfig.DEFAULT_SERIALIZES.get(type));
		}
	}

	@Override
	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {

		HBaseAggregateState state = new HBaseAggregateState(config);
		CachedMap cachaMap = new CachedMap(state, config.getStateCacheSize());

		MapState ms = null;
		if (type == StateType.NON_TRANSACTIONAL) {
			ms = NonTransactionalMap.build(cachaMap);
		} else if (type == StateType.OPAQUE) {
			ms = OpaqueMap.build(cachaMap);
		} else if (type == StateType.TRANSACTIONAL) {
			ms = TransactionalMap.build(cachaMap);
		}
		return new SnapshottableMap(ms, new Values("$GLOBAL$"));
	}

}
