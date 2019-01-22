package cloudy.trident.drpc;

import org.apache.thrift7.TException;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;
import backtype.storm.utils.Utils;

public class TridentDrpcClient {

	public static void main(String[] args) {

		DRPCClient drpc = new DRPCClient("192.168.60.134", 3772);

		while (true) {
			try {
				String getOrderAmt = drpc.execute("getOrderAmt",
						"2019-01-18:cf:orderamt_1 2019-01-18:cf:orderamt_2 2019-01-18:cf:orderamt_3 2019-01-18:cf:orderamt_4 2019-01-18:cf:orderamt_5 2019-01-18:cf:orderamt_6 2019-01-18:cf:orderamt_7 2019-01-18:cf:orderamt_8");
				System.out.println("######################################################");
				System.out.println("#### 销售额： getOrderAmt = " + getOrderAmt);

				String getOrderNum = drpc.execute("getOrderNum",
						"2019-01-18:cf:ordernum_1 2019-01-18:cf:ordernum_2 2019-01-18:cf:ordernum_3 2019-01-18:cf:ordernum_4 2019-01-18:cf:ordernum_5 2019-01-18:cf:ordernum_6 2019-01-18:cf:ordernum_7 2019-01-18:cf:ordernum_8");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				System.out.println("@@@@ 订单数： getOrderNum = " + getOrderNum);

				Utils.sleep(3000);

			} catch (TException e) {
				e.printStackTrace();
			} catch (DRPCExecutionException e) {
				e.printStackTrace();
			}
		}

	}

}
