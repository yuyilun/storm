package com.hadoop.storm.user_visits;

import com.hadoop.storm.base.SourceSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * main progress
 * 
 * @author yu100
 *
 */
public class UVTopo {

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new SourceSpout(), 1);
		builder.setBolt("bolt1", new FmtLogBolt(), 4).shuffleGrouping("spout");
		builder.setBolt("bolt2", new DeepVisitBolt(), 4).fieldsGrouping("bolt1", new Fields("date","session_id"));
		builder.setBolt("bolt3", new UVSumBolt(), 1).shuffleGrouping("bolt2");
		

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
