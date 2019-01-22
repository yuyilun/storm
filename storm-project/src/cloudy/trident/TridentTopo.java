package cloudy.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Fields;
import cloudy.hbase.state.HBaseAggregateState;
import cloudy.hbase.state.TridentConfig;
import cloudy.trident.tools.OrderAmtSplit;
import cloudy.trident.tools.OrderNumSplit;
import cloudy.trident.tools.Split;
import cloudy.trident.tools.SplitBy;
import storm.kafka.BrokerHosts;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.FirstN;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.state.StateFactory;

public class TridentTopo {
	
	public static StormTopology builder(LocalDRPC drpc) {
		
		BrokerHosts hosts = new ZkHosts("192.168.60.134:2181,192.168.60.135:2181");
		String topic = "track";

		TridentKafkaConfig config = new TridentKafkaConfig(hosts, topic);
		config.forceFromStart = false;// test
		config.fetchSizeBytes =100;//transaction batch size
		config.scheme = new SchemeAsMultiScheme(new StringScheme());
		//数据源，kafka consumer 输出字段和值:new Fields("str"),new Values(msg)
		TransactionalTridentKafkaSpout spout = new TransactionalTridentKafkaSpout(config);
		
		TridentConfig tridentConfig = new TridentConfig("count_order");
		StateFactory state = HBaseAggregateState.transational(tridentConfig);
		
		TridentTopology topology = new TridentTopology();
		//销售额
		TridentState amtState = topology.newStream("spout", spout)
				.parallelismHint(3)
				.each(new Fields(StringScheme.STRING_SCHEME_KEY),new OrderAmtSplit("\t"), new Fields("order_id","order_amt","create_date","province_id","cf"))
				.shuffle()
				.groupBy(new Fields("create_date","cf","province_id"))
				.persistentAggregate(state,new Fields("order_amt") ,new Sum(), new Fields("sum_amt"));
		
		topology.newDRPCStream("getOrderAmt", drpc)
			.each(new Fields("args"), new Split(" "), new Fields("arg"))
			.each(new Fields("arg"), new SplitBy(":"),new Fields("create_date","cf","province_id"))
			.groupBy(new Fields("create_date","cf","province_id"))
			.stateQuery(amtState,new Fields("create_date","cf","province_id"), new MapGet(), new Fields("order_amt"))
			.each(new Fields("order_amt"),new FilterNull())
			.applyAssembly(new FirstN(5, "order_amt", true))
			;
		
		
		//**********************************************************************/
		//订单数
		TridentState orderState = topology.newStream("orderSpout", spout)
				.parallelismHint(3)
				.each(new Fields(StringScheme.STRING_SCHEME_KEY),new OrderNumSplit("\t"), new Fields("order_id","order_amt","create_date","province_id","cf"))
				.shuffle()
				.groupBy(new Fields("create_date","cf","province_id"))
				.persistentAggregate(state,new Fields("order_id") ,new Count(), new Fields("order_count"));
		
		topology.newDRPCStream("getOrderNum", drpc)
			.each(new Fields("args"), new Split(" "), new Fields("arg"))
			.each(new Fields("arg"), new SplitBy(":"),new Fields("create_date","cf","province_id"))
			.groupBy(new Fields("create_date","cf","province_id"))
			.stateQuery(orderState,new Fields("create_date","cf","province_id"), new MapGet(), new Fields("order_count"))
			.each(new Fields("order_count"),new FilterNull())
			//.applyAssembly(new FirstN(5, "order_count", true))
			;
		
		return topology.build();
		
	}
	
	
	public static void main(String[] args) {
		Config conf = new Config();
		conf.setDebug(false);
		
		LocalDRPC drpc = new LocalDRPC();
		if (args.length > 0) {
			//
			try {
				StormSubmitter.submitTopology(args[0], conf, builder(null));
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			// local
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("mytopo", conf, builder(drpc));
		}
		
	}

}
