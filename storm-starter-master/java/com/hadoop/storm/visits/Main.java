package com.hadoop.storm.visits;

import com.hadoop.storm.lession.Myspout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

/**
 * main progress
 * 
 * @author yu100
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new Myspout(), 1);

		//
		/**
		 * 分组策略6中 Shuffle Grouping：轮询，平均分配 Non Grouping: 无分组 Fields Grouping :按Field分组
		 * All Grouping： 广播发送 Global Grouping:
		 * 全局分组.这个tuple被分配到storm中的一个bolt的其中一个task。再具体一点就是分配给id值最低的那个task Direct
		 * Grouping: 直接分组.这是一种比较特别的分组方法，用这种分组意味着消息的发送者决定由消息接收者的哪个task处理这个消息。
		 * 只有被声明为Direct Stream的消息流可以声明这种分组方法。而且这种消息tuple必须使用emitDirect方法来发射。
		 * 消息处理者可以通过TopologyContext来或者处理它的消息的taskid (OutputCollector.emit方法也会返回taskid)
		 */
		// builder.setBolt("bolt", new Mybolt(), 1).shuffleGrouping("spout");
		// builder.setBolt("bolt", new Mybolt(), 2).noneGrouping("spout");
		// builder.setBolt("bolt", new Mybolt(), 2).fieldsGrouping("spout", new
		// Fields("log"));
		// builder.setBolt("bolt", new Mybolt(), 2).allGrouping("spout");
		// builder.setBolt("bolt", new Mybolt(), 2).globalGrouping("spout");
		// builder.setBolt("bolt", new Mybolt(), 2).directGrouping("spout");

		//builder.setBolt("bolt", new PVBolt(), 2).shuffleGrouping("spout");
		builder.setBolt("bolt1", new PVBolt1(), 4).shuffleGrouping("spout");
		builder.setBolt("bolt2", new PVSumBolt(), 1).shuffleGrouping("bolt1");
		

		Config conf = new Config() ;
		conf.setDebug(true);

		if (args.length > 0) {
			//
			StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		} else {
			// local
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("mytopology", conf, builder.createTopology());
		}

	}

}
