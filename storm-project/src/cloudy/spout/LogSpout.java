package cloudy.spout;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumers.OrderConsumer;

public class LogSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Queue<String> queue = new ConcurrentLinkedQueue<String>();
	SpoutOutputCollector collector = null;
	String topic = null;

	public LogSpout(String topic) {
		this.topic = topic;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		OrderConsumer consumer = new OrderConsumer(topic);
		consumer.start();
		queue = consumer.getQueue();
		this.collector = collector;

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
			System.out.println("################## LogSpout nextTuple() : " + str);
			collector.emit(new Values(str), UUID.randomUUID());
		}

	}

	@Override
	public void ack(Object msgId) {
		// 通常用于删除已经成功处理的tuple
		// 我们这里不用实现
	}

	@Override
	public void fail(Object msgId) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
