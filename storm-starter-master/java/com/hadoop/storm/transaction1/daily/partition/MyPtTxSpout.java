package com.hadoop.storm.transaction1.daily.partition;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hadoop.storm.transaction1.MyMata;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.transactional.partitioned.IPartitionedTransactionalSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class MyPtTxSpout implements IPartitionedTransactionalSpout<MyMata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int BATCH_NUM = 10;

	public Map<Integer, Map<Long, String>> PT_DATA_MP = new HashMap<Integer, Map<Long, String>>();

	public MyPtTxSpout() {

		Random random = new Random();

		String[] hosts = { "www.taobao.com" };
		String[] session_id = { "ABYH6Y4V4SCVXTG6DPB4VH9U123", "XXYH6YCGFJYERTT834R52FDXV9U34",
				"BBYH61456FGHHJ7JL89RG5VV9UYU7", "CYYH6Y2345GHI899OFG4V9U567", "VVVYH6Y4V4SFXZ56JIPDPB4V678" };
		String[] time = { "2014-01-07 08:40:50", "2014-01-07 08:40:51", "2014-01-07 08:40:52", "2014-01-07 08:40:53",
				"2014-01-07 09:40:49", "2014-01-07 10:40:49", "2014-01-07 11:40:49", "2014-01-07 12:40:49" };

		for (int j = 0; j < 5; j++) {
			HashMap<Long, String> map = new HashMap<Long, String>();
			for (long i = 0; i < 100; i++) {
				map.put(i, hosts[0] + "\t" + session_id[random.nextInt(5)] + "\t" + time[random.nextInt(8)]);
			}
			PT_DATA_MP.put(j, map);
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tx", "log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public Coordinator getCoordinator(Map conf, TopologyContext context) {
		return new MyCoordinator();
	}

	@Override
	public Emitter<MyMata> getEmitter(Map conf, TopologyContext context) {
		return new MyEmitter();
	}

	public class MyCoordinator implements IPartitionedTransactionalSpout.Coordinator {
		@Override
		public int numPartitions() {
			return 5;
		}

		@Override
		public boolean isReady() {
			Utils.sleep(1000);
			return true;
		}

		@Override
		public void close() {
		}
	}

	public class MyEmitter implements IPartitionedTransactionalSpout.Emitter<MyMata> {

		@Override
		public MyMata emitPartitionBatchNew(TransactionAttempt tx, BatchOutputCollector collector, int partition,
				MyMata lastPartitionMeta) {

			long beginPoint = 0;
			if (lastPartitionMeta == null) {
				beginPoint = 0;
			} else {
				beginPoint = lastPartitionMeta.getBeginPoint() + lastPartitionMeta.getNum();
			}

			MyMata mata = new MyMata();
			mata.setBeginPoint(beginPoint);
			mata.setNum(BATCH_NUM);

			emitPartitionBatch(tx, collector, partition, mata);

			System.err.println("启动一个事务： " + mata.toString());
			return mata;
		}

		@Override
		public void emitPartitionBatch(TransactionAttempt tx, BatchOutputCollector collector, int partition,
				MyMata partitionMeta) {

			System.err.println("emitPartitionBatch partition : " + partition);

			long beginPoint = partitionMeta.getBeginPoint();
			int num = partitionMeta.getNum();

			Map<Long, String> batchMap = PT_DATA_MP.get(partition);

			for (long i = beginPoint; i < num + beginPoint; i++) {
				if (batchMap.get(i) == null) {
					break;
				}
				collector.emit(new Values(tx, batchMap.get(i)));
			}
		}

		@Override
		public void close() {

		}

	}

}
