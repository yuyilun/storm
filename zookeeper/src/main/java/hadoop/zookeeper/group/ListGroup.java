package hadoop.zookeeper.group;

import java.util.List;
import org.apache.zookeeper.KeeperException;
import hadoop.zookeeper.util.ConnectionWatcher;

/**
 * 展示组
 * @author yu100
 *
 */
public class ListGroup extends ConnectionWatcher{
	
	 public void list(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;

        try {
            List<String> children = zk.getChildren(path, false);
            if (children.isEmpty()) {
                System.out.println("No members in group {}"+ groupName);
                return;
            }
            for (String child : children) {
            	System.out.println(child);
            }
        } catch (KeeperException.NoNodeException e) {
        	System.out.println("Group {} does not exist"+ groupName+ e);
        }
    }

    public static void main(String[] args) throws Exception {
        ListGroup listGroup = new ListGroup();
        listGroup.connect(args[0]);
        listGroup.list(args[1]);
        listGroup.close();
    }
	
	
}
