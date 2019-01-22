package hadoop.zookeeper.group;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import hadoop.zookeeper.util.ConnectionHelper;

/**
 * 无限循环 获取组
 * @author yu100
 *
 */
public class ListGroupForever {
	
	private final ZooKeeper zooKeeper;
	private final Semaphore semaphore = new Semaphore(1);
	public ListGroupForever(ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;
	}
	
	public void listForever(String groupName) throws InterruptedException, KeeperException {
		semaphore.acquire();
		while(true) {
			list(groupName);
			semaphore.acquire();
		}
	}
	
	public void list(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		List<String> children = zooKeeper.getChildren(path, event -> {
			if(event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
				semaphore.release();
			}
		});
		
		if(children.isEmpty()) {
			System.out.println("No members in group {}"+ groupName);
			return;
		}
		
		Collections.sort(children);//排序
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
		ZooKeeper zk = new ConnectionHelper().connect(args[0]);
        new ListGroupForever(zk).listForever(args[1]);
	}
	
	
	
	

}
