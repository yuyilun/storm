package com.hadoop.storm.transaction1.daily;

import com.hadoop.storm.transaction1.MyTxSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.transactional.TransactionalTopologyBuilder;

public class MyDailyTopo {

	public static void main(String[] args) {

		TransactionalTopologyBuilder builder = new TransactionalTopologyBuilder("ttbid", "spoutId", new MyTxSpout(), 1);

		builder.setBolt("bolt1", new MyDailyBatchBolt(), 3).shuffleGrouping("spoutId");
		builder.setBolt("committer", new MyDailyCommitterBolt(), 1).shuffleGrouping("bolt1");

		Config conf = new Config();
		conf.setDebug(true);

		if (args.length > 0) {
			//
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.buildTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			// local
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("mytopology", conf, builder.buildTopology());
		}

	}

}
