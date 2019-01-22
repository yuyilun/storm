package hadoop.zookeeper.group;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建组
 * 执行脚本：
 * export CLASSPATH=.:xx/xxx/classes/:$ZOOKEEPER_HOME/*:$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
 * java CreateGroup 10.10.11.11 zoo
 * 
 * @author yu100
 *
 */
public class CreateGroup implements Watcher{
	
	
	private static final int SESSION_TIMEOUT = 5000;
	private ZooKeeper zk;
	//
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	/**
	 * 连接
	 * @param hosts
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void connect(String hosts) throws IOException, InterruptedException {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
		connectedSignal.await();//等待确定连接上zk服务
	}
	
	@Override
	public void process(WatchedEvent event) {
		if(event.getState() == KeeperState.SyncConnected) {
			connectedSignal.countDown();
		}
	}
	
	public void create(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		String createdPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("createdPath: " + createdPath);
	}
	
	public void close() throws InterruptedException {
		zk.close();
	}
	
	public static void main(String[] args) throws Exception {
		CreateGroup createGroup = new CreateGroup();
		createGroup.connect(args[0]);
		createGroup.create(args[1]);
		createGroup.close();
	}

}
