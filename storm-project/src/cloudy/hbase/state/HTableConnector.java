package cloudy.hbase.state;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;

public class HTableConnector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Configuration configuration;

	protected HTableInterface table;

	private String tableName;
	HConnection htablePool = null;

	public HTableConnector(TupleTableConfig conf){
		configuration = new Configuration();
		String zk_list = "192.168.60.134,192.168.60.135";
		configuration.set("hbase.zookeeper.quorum", zk_list);
		this.tableName = conf.getTableName();
		try {
			this.htablePool = HConnectionManager.createConnection(configuration);
			this.table = this.htablePool.getTable(tableName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public HTableInterface getTable() {
		return table;	
	}

	public void setTable(HTableInterface table) {
		this.table = table;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void close() {
		try {
			this.table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
