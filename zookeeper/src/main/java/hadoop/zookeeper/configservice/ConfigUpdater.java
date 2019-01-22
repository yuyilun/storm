package hadoop.zookeeper.configservice;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 配置文件更新
 *
 */
public class ConfigUpdater 
{
	
	public static final String PATH = "/config";
	
	private final ActiveKeyValueStore store;
	private final Random random = new Random();
	
	public ConfigUpdater(String hosts) throws Exception {
		store = new ActiveKeyValueStore();
		store.connect(hosts);
	}
	
	public void run() throws Exception {
		while(true) {
			int value = random.nextInt(100);
			store.write(PATH, String.valueOf(value));
			
			TimeUnit.SECONDS.sleep(random.nextInt(10));//休眠
		}
	}
	
	
	/**
	 * 启动
	 * @param args
	 * @throws Exception
	 */
    public static void main( String[] args ) throws Exception
    {
        ConfigUpdater updater = new ConfigUpdater(args[0]);
        updater.run();
    }
}
