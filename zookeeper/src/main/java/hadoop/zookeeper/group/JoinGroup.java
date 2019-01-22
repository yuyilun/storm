package hadoop.zookeeper.group;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import hadoop.zookeeper.util.ConnectionWatcher;
/**
 * 加入组
 * @author yu100
 */
public class JoinGroup extends ConnectionWatcher{
	
	public void join(String groupName, String memberName) throws KeeperException, InterruptedException {
		String path = "/" + groupName  + "/" + memberName;
		String createdPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("createdPath： " + createdPath);
	}
	
	public static void main(String[] args) throws Exception {
		JoinGroup joinGroup = new JoinGroup();
		joinGroup.connect(args[0]);
		joinGroup.join(args[1], args[2]);
		Thread.sleep(Long.MAX_VALUE);
	}

}
