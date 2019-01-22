package cloudy.hbase.dao.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import cloudy.hbase.dao.HBaseDao;

public class HBaseDaoImp implements HBaseDao {

	HConnection htablePool = null;

	public HBaseDaoImp() {
		// init()
		Configuration conf = new Configuration();
		String zk_hosts = "192.168.60.134,192.168.60.135";
		conf.set("hbase.zookeeper.quorum", zk_hosts);

		try {
			htablePool = HConnectionManager.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void save(Put put, String tableName) {
		HTableInterface table = null;
		try {
			table = htablePool.getTable(tableName);
			table.put(put);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void save(List<Put> puts, String tableName) {
		HTableInterface table = null;
		try {
			table = htablePool.getTable(tableName);
			table.put(puts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Result getOneRow(String tableName, String rowKey) {

		HTableInterface table = null;
		Result result = null;
		try {
			table = htablePool.getTable(tableName);

			Get get = new Get(Bytes.toBytes(rowKey));
			result = table.get(get);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public List<Result> getRows(String tableName, String rowKey_like) {

		HTableInterface table = null;
		List<Result> results = null;
		try {
			table = htablePool.getTable(tableName);
			PrefixFilter filter = new PrefixFilter(Bytes.toBytes(rowKey_like));
			Scan scan = new Scan();
			scan.setFilter(filter);
			ResultScanner scanner = table.getScanner(scan);
			results = new ArrayList<Result>();
			for (Result rs : scanner) {
				results.add(rs);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	@Override
	public List<Result> getRows(String tableName, String rowKey_like, String[] cols) {

		HTableInterface table = null;
		List<Result> results = null;
		try {
			table = htablePool.getTable(tableName);
			PrefixFilter filter = new PrefixFilter(Bytes.toBytes(rowKey_like));
			Scan scan = new Scan();
			scan.setFilter(filter);
			for (int i = 0; i < cols.length; i++) {
				scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes(cols[i]));
			}
			ResultScanner scanner = table.getScanner(scan);
			results = new ArrayList<Result>();
			for (Result rs : scanner) {
				results.add(rs);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	@Override
	public List<Result> getRows(String tableName, String startRow, String stopRow) {

		HTableInterface table = null;
		List<Result> results = null;
		try {
			table = htablePool.getTable(tableName);
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes(startRow));
			scan.setStopRow(Bytes.toBytes(stopRow));
			ResultScanner scanner = table.getScanner(scan);
			results = new ArrayList<Result>();
			for (Result rs : scanner) {
				results.add(rs);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	@Override
	public void insert(String tableName, String rowKey, String family, String quailifer, String value) {
		HTableInterface table = null;
		try {
			table = htablePool.getTable(tableName);
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(quailifer), Bytes.toBytes(value));
			table.put(put);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void insert(String tableName, String rowKey, String family, String[] quailifers, String[] values) {
		HTableInterface table = null;
		try {
			table = htablePool.getTable(tableName);
			Put put = new Put(Bytes.toBytes(rowKey));

			for (int i = 0; i < quailifers.length; i++) {
				put.add(Bytes.toBytes(family), Bytes.toBytes(quailifers[i]), Bytes.toBytes(values[i]));
			}

			table.put(put);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// check
		HBaseDao hBaseDao = new HBaseDaoImp();
		// Put put = new Put("testrow".getBytes());
		// put.add("cf".getBytes(), "name".getBytes(), "zhangsan".getBytes());
		// hBaseDao.save(put, "test");
		// put.add("cf".getBytes(), "addr".getBytes(), "beijing".getBytes());
		// hBaseDao.save(put, "test");

		// List<Put> puts = new ArrayList<Put>();
		// Put put = new Put("testrow3".getBytes());
		// put.add("cf".getBytes(), "age".getBytes(), "14".getBytes());
		// puts.add(put);
		// put.add("cf".getBytes(), "name".getBytes(), "zhangsan".getBytes());
		// puts.add(put);
		// hBaseDao.save(puts, "test");

		// Result result = hBaseDao.getOneRow("test", "testrow2");
		//
		// for (Cell cell : result.listCells()) {
		// System.out.println("family : " + Bytes.toString(cell.getFamily()) + " ;
		// Qualifier : "
		// + Bytes.toString(cell.getQualifier()) + " ; Value : " +
		// Bytes.toString(cell.getValue()));
		// }

		// List<Result> rows = hBaseDao.getRows("test", "test");
		// for (Result rs : rows) {
		// for (KeyValue kv : rs.raw()) {
		// System.out.println(
		// "rowkey : " + Bytes.toString(kv.getRow()) +
		// " ; family : " + Bytes.toString(kv.getFamily()) +
		// " ; Qualifier : "+ Bytes.toString(kv.getQualifier()) +
		// " ; Value : " + Bytes.toString(kv.getValue()));
		// }
		// }

		// List<Result> rows = hBaseDao.getRows("test", "test",new String[]
		// {"name","age"});
		// for (Result rs : rows) {
		// for (KeyValue kv : rs.raw()) {
		// System.out.println(
		// "rowkey : " + Bytes.toString(kv.getRow()) +
		// " ; family : " + Bytes.toString(kv.getFamily()) +
		// " ; Qualifier : "+ Bytes.toString(kv.getQualifier()) +
		// " ; Value : " + Bytes.toString(kv.getValue()));
		// }
		// }

		List<Result> rows = hBaseDao.getRows("test", "testrow", "testrow4");
		for (Result rs : rows) {
			for (KeyValue kv : rs.raw()) {
				System.out.println("rowkey : " + Bytes.toString(kv.getRow()) + " ; family : "
						+ Bytes.toString(kv.getFamily()) + " ; Qualifier : " + Bytes.toString(kv.getQualifier())
						+ " ; Value : " + Bytes.toString(kv.getValue()));
			}
		}

		// hBaseDao.insert("test", "testrow2", "cf", "age", "23");
		// hBaseDao.insert("test", "testrow2", "cf", "addr", "shanghai");
		// hBaseDao.insert("test", "testrow2", "cf", "name", "mawu");
		// hBaseDao.insert("test", "testrow2", "cf", "tel", "180");

	}

}
