package hadoop.zookeeper.configservice;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 配置文件-观察者
 * @author yu100
 *
 */
public class ConfigWatcher implements Watcher{
	
	private final ActiveKeyValueStore store;

    public ConfigWatcher(String hosts) throws Exception {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }
	
    
    public void displayConfig() throws Exception {
    	/**
    	 * 设置观察者
    	 */
    	store.read(ConfigUpdater.PATH, this);
    }
    
    /**
     * 事件通知，触发方法
     */
	public void process(WatchedEvent event) {
		if (event.getType() == Event.EventType.NodeDataChanged) {
			 try {
				displayConfig();
			} catch (Exception e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
	
	 public static void main(String[] args) throws Exception {
        ConfigWatcher watcher = new ConfigWatcher(args[0]);
        watcher.displayConfig();

        Thread.sleep(Long.MAX_VALUE);
	 }


}
