package demo;

import java.util.ArrayList;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class MyKafkaSpout {
	
	
	
	public static void main(String[] args) {
		
		String topic = "track";
		
		ZkHosts zkHosts = new ZkHosts("192.168.60.134:2181,192.168.60.135:2181");
		
		SpoutConfig spoutConfig = new SpoutConfig(zkHosts, 
				topic,
				"/MyKafka", //offset 偏移量的根目录
				"MyTrack"); //对应一个应用
		
		List<String> zkServers = new ArrayList<String>();
		//zkServers.add("192.168.60.134");
		//zkServers.add("192.168.60.135");
		
		for(String host : zkHosts.brokerZkStr.split(",")) {
			zkServers.add(host.split(":")[0]);
		}
		
		spoutConfig.zkServers = zkServers;
		spoutConfig.zkPort = 2181;
		
		spoutConfig.forceFromStart = true;
		spoutConfig.socketTimeoutMs = 60 * 1000;
		
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new KafkaSpout(spoutConfig),1);
		builder.setBolt("bolt", new MyKafkaBolt(),1).shuffleGrouping("spout");
		
		Config conf = new Config() ;
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
