package hadoop.zookeeper.group;

import java.util.concurrent.CountDownLatch;

import hadoop.zookeeper.util.ConnectionWatcher;

public class AsyncListGroup extends ConnectionWatcher{
	
	
	
	public void list(final String groupName) throws InterruptedException {
		String path = "/"+groupName;
		final CountDownLatch latch = new CountDownLatch(1);
		zk.getChildren(path, false,
			(rc,path1,ctx,children) -> {
				System.out.println("Called back for path {} with return code {}" + path1 + rc);
				if(children == null) {
					System.out.println("Group"
							+ " {} does not exist" + groupName);
				}else {
					if (children.isEmpty()) {
						System.out.println("No members in group {}" + groupName);
                    	return;
                    }
                    for (String child : children) {
                    	System.out.println(child);
                    }
				}
		} , null);
		System.out.println("Awaiting latch countdown...");
       latch.await();
	}

}
