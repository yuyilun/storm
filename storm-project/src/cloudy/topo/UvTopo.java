package cloudy.topo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import cloudy.bolt.LogFmtBolt;
import cloudy.bolt.UVBolt1;
import cloudy.bolt.UVRsltBolt;
import cloudy.spout.LogSpout;
import kafka.KafkaProperties;

public class UvTopo {
	
	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new LogSpout(KafkaProperties.Log_topic), 3);//获取数据
		builder.setBolt("fmtBolt", new LogFmtBolt(), 3).shuffleGrouping("spout");//格式化数据
		builder.setBolt("UVBolt1", new UVBolt1() , 5).fieldsGrouping("fmtBolt", new Fields("date","session_id")) ;
		
		builder.setBolt("rsltBolt", new UVRsltBolt(), 1).shuffleGrouping("UVBolt1");
		
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(10);
		if (args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		}else {
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("mytopology", conf, builder.createTopology());
		}
	}

}
