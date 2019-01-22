package com.hadoop.storm.transaction1;

import java.math.BigInteger;
import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Values;

public class MyEmitter implements ITransactionalSpout.Emitter<MyMata> {
	Map<Long, String> map = null;

	public MyEmitter(Map<Long, String> map) {
		this.map = map;
	}

	@Override
	public void emitBatch(TransactionAttempt tx, MyMata coordinatorMeta, BatchOutputCollector collector) {
		long beginPoint = coordinatorMeta.getBeginPoint();
		int num = coordinatorMeta.getNum();

		for (long i = beginPoint; i < num + beginPoint; i++) {
			if (map.get(i) == null) {
				continue;
			}
			collector.emit(new Values(tx, map.get(i)));
		}
	}

	@Override
	public void cleanupBefore(BigInteger txid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
