package com.hadoop.storm.wordcount;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This topology demonstrates Storm's stream groupings and multilang
 * capabilities.
 */
public class WordCountTopology {
	/*
	 * public static class SplitSentence extends ShellBolt implements IRichBolt {
	 * 
	 * public SplitSentence() { super("python", "splitsentence.py"); }
	 * 
	 * @Override public void declareOutputFields(OutputFieldsDeclarer declarer) {
	 * declarer.declare(new Fields("word")); }
	 * 
	 * @Override public Map<String, Object> getComponentConfiguration() { return
	 * null; } }
	 */

	public static class WordCount extends BaseBasicBolt {
		Map<String, Integer> counts = new HashMap<String, Integer>();

		@Override
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			String word = tuple.getString(0);
			Integer count = counts.get(word);
			if (count == null)
				count = 0;
			count++;
			counts.put(word, count);

			System.err.println(Thread.currentThread().getName() + " ; word= " + word + " ; count= " + count);

			collector.emit(new Values(word, count));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}
	}

	/**
	 * 分组策略6中 Shuffle Grouping：轮询，平均分配
	 *  Non Grouping: 无分组 
	 *  Fields Grouping :按Field分组
	 * All Grouping： 广播发送
	 *  Global Grouping:
	 * 全局分组.这个tuple被分配到storm中的一个bolt的其中一个task。再具体一点就是分配给id值最低的那个task Direct
	 * Direct Grouping: 直接分组.这是一种比较特别的分组方法，用这种分组意味着消息的发送者决定由消息接收者的哪个task处理这个消息。
	 * 只有被声明为Direct Stream的消息流可以声明这种分组方法。而且这种消息tuple必须使用emitDirect方法来发射。
	 * 消息处理者可以通过TopologyContext来或者处理它的消息的taskid (OutputCollector.emit方法也会返回taskid)
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new MyRandomSentenceSpout(), 1);

		builder.setBolt("split", new Mysplit(" "), 8).shuffleGrouping("spout");

		builder.setBolt("count", new WordCount(), 3).fieldsGrouping("split", new Fields("word"));
		// builder.setBolt("count", new WordCount(), 12).shuffleGrouping("split");

		builder.setBolt("sum", new SumBolt(), 1).shuffleGrouping("count");

		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);

			StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("word-count", conf, builder.createTopology());

			/*
			 * Thread.sleep(10000); cluster.shutdown();
			 */
		}
	}
}
