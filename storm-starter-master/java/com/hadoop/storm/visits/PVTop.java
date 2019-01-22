package com.hadoop.storm.visits;

import com.hadoop.storm.base.SourceSpout;

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
public class PVTop {

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new SourceSpout(), 1);
		builder.setBolt("bolt1", new PVBolt(), 4).shuffleGrouping("spout");

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
