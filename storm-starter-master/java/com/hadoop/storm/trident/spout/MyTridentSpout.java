package com.hadoop.storm.trident.spout;

import java.util.Map;

import com.hadoop.storm.transaction1.MyMata;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout;
import storm.trident.topology.TransactionAttempt;

public class MyTridentSpout implements ITridentSpout<MyMata>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public BatchCoordinator<MyMata> getCoordinator(String txStateId, Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Emitter<MyMata> getEmitter(String txStateId, Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fields getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class MyCoordinator implements BatchCoordinator<MyMata>{
		@Override
		public MyMata initializeTransaction(long txid, MyMata prevMetadata, MyMata currMetadata) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void success(long txid) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isReady(long txid) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
	}
	public class MyEmitter implements Emitter<MyMata>{

		@Override
		public void emitBatch(TransactionAttempt tx, MyMata coordinatorMeta, TridentCollector collector) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void success(TransactionAttempt tx) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
	}

}

	
