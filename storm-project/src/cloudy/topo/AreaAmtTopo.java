package cloudy.topo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import cloudy.bolt.AreaAmtBolt;
import cloudy.bolt.AreaFilterBolt;
import cloudy.bolt.AreaRsltBolt;
import cloudy.spout.OrderBaseSpout;
import kafka.KafkaProperties;

public class AreaAmtTopo {

	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new OrderBaseSpout(KafkaProperties.order_topic), 5);
		builder.setBolt("filter", new AreaFilterBolt(), 5).shuffleGrouping("spout");
		builder.setBolt("areabolt", new AreaAmtBolt(), 5).fieldsGrouping("filter", new Fields("area_id"));
		builder.setBolt("rsltbolt", new AreaRsltBolt(),1).shuffleGrouping("areabolt");
		
		Config conf = new Config();
		conf.setDebug(false);

		if (args.length > 0) {
			//
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			// local
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("mytopology", conf, builder.createTopology());
		}

	}
}
