package cloudy.spout;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumers.OrderConsumer;

public class OrderBaseSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Queue<String> queue = new ConcurrentLinkedQueue<String>();
	SpoutOutputCollector collector = null;
	Integer taskId = null;
	String topic = null;
	
	
	public OrderBaseSpout(String topic ) {
		this.topic = topic;
	}

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		OrderConsumer consumer = new OrderConsumer(topic);
		consumer.start();
		queue = consumer.getQueue();
		this.collector = collector;
		taskId = context.getThisTaskId();
	}

	@Override
	public void close() {
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {

	}

	@Override
	public void nextTuple() {
		if (queue.size() > 0) {
			String str = queue.poll();
			System.out.println("taskId : " + taskId + " ; str= " +str);
			collector.emit(new Values(str));
		}
	}

	@Override
	public void ack(Object msgId) {
	}

	@Override
	public void fail(Object msgId) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("order"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
