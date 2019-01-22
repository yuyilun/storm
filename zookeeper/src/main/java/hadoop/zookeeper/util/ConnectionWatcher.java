package hadoop.zookeeper.util;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 连接zk服务
 * @author yu100
 *
 */
public class ConnectionWatcher implements Watcher{
	
	private static final int SESSION_TIMEOUT = 5000;
	protected ZooKeeper zk;
	//倒计数门闩，利用计数器实现的
	private final CountDownLatch connectedSignal = new CountDownLatch(1);
	
	public void connect(String host) throws Exception {	
		zk = new ZooKeeper(host, SESSION_TIMEOUT, this);
		connectedSignal.await();//等待
	} 
	
	//事件通知
	public void process(WatchedEvent event) {
		if(event.getState() == Event.KeeperState.SyncConnected) {
			connectedSignal.countDown();//释放
		}
	}
	
	public void close() throws Exception {
		zk.close();
	}
	
}