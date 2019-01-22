package hadoop.zookeeper.group;

import java.util.List;

import org.apache.zookeeper.KeeperException;

import hadoop.zookeeper.util.ConnectionWatcher;

/**
 * 删除组
 * @author yu100
 *
 */
public class DeleteGroup extends ConnectionWatcher{
	
	public void delete(String groupName) throws KeeperException, InterruptedException {
		
		String path = "/" + groupName;
		
		List<String> children = zk.getChildren(path, false);
		for(String child : children) {
			zk.delete(path + "/" + child, -1);
		}
		
		zk.delete(path, -1);
	}
	
	public static void main(String[] args) throws Exception {
		DeleteGroup deleteGroup = new DeleteGroup();
        deleteGroup.connect(args[0]);
        deleteGroup.delete(args[1]);
        deleteGroup.close();
	}
	
	
	
	

}
