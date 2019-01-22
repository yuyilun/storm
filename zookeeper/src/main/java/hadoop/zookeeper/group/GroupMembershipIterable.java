package hadoop.zookeeper.group;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import hadoop.zookeeper.util.ConnectionHelper;

public class GroupMembershipIterable implements Iterable<List<String>>{
	
	private final ZooKeeper zooKeeper;
    private final String groupName;
    private final String groupPath;
    private final Semaphore semaphore = new Semaphore(1);
    
	@Override
	public Iterator<List<String>> iterator() {
		return new Iterator<List<String>>() {
			@Override
			public boolean hasNext() {
				try {
					semaphore.acquire();
					return zooKeeper.exists(groupPath, false) != null;
				}catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                }
			}

			@Override
			public List<String> next() {
				 try {
					return list(groupName);
				}  catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                }
			}
		};
	}

	public GroupMembershipIterable(ZooKeeper zooKeeper, String groupName) {
		this.zooKeeper = zooKeeper;
		this.groupName = groupName;
		groupPath = pathFor(groupName);
	}
	
	private String pathFor(String groupName) {
		return "/" + groupName;
	}
	
	private boolean isNodeDeletedEventForGroup(WatchedEvent event) {
		return event.getType() == Watcher.Event.EventType.NodeDeleted && event.getPath().equals(groupPath);
	}
	
	private boolean isNodeChildrenChangedEvent(WatchedEvent event) {
		return event.getType() == Watcher.Event.EventType.NodeChildrenChanged;
	}
	
	private List<String> list(final String groupName) throws KeeperException, InterruptedException{
		String path = pathFor(groupName);
		List<String> children = zooKeeper.getChildren(path, event -> {
			if (isNodeChildrenChangedEvent(event) || isNodeDeletedEventForGroup(event)) {
                semaphore.release();
            }
		});
		Collections.sort(children);
	    return children;
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ZooKeeper zk = new ConnectionHelper().connect(args[0]);
        String theGroupName = args[1];
        GroupMembershipIterable iterable = new GroupMembershipIterable(zk, theGroupName);
        Iterator<List<String>> iterator = iterable.iterator();
        while (iterator.hasNext()) {
           System.out.println("{}"+ iterator.next());
           System.out.println("--------------------");
        }
        System.out.println("Group {} does not exist (anymore)!"+theGroupName);
	}
	

}
