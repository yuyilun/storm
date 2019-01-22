package hadoop.zookeeper.configservice;

import java.nio.charset.Charset;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import hadoop.zookeeper.util.ConnectionWatcher;

public class ActiveKeyValueStore extends ConnectionWatcher{
	
	
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	/**
	 * 更新数据
	 * @param path
	 * @param value
	 * @throws Exception
	 */
	public void write(String path, String value) throws Exception {
		Stat stat = zk.exists(path, false);
		if(stat == null) {
			zk.create(path, value.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}else {
			zk.setData(path, value.getBytes(CHARSET), -1);
		}
	}
	
	/**
	 * 读取数据
	 * @param path
	 * @param watcher
	 * @return
	 * @throws Exception
	 */
	public String read(String path,Watcher watcher) throws Exception {
		byte[] data = zk.getData(path, watcher, null);
		return new String(data,CHARSET);
	}
	
	
	
}
