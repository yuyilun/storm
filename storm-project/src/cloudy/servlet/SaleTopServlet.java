package cloudy.servlet;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift7.TException;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;
import backtype.storm.utils.Utils;
import cloudy.tools.DataUtils;

/**
 * 销售额top5
 */
@WebServlet("/SaleTopServlet")
public class SaleTopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaleTopServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DRPCClient drpc = new DRPCClient("192.168.60.134", 3772);
		Map<String, String> provinceMap = DataUtils.getProvinceMap();
		try {
			while (true) {
				String todayColumn = "[";
				String todaySpline = "[";
				String provinceName = "[";
				List<String> provinceList = new ArrayList<String>();
				String getOrderAmt = drpc.execute("getOrderAmt", DataUtils.getInputMsg(null, "cf", "orderamt", 8));
				System.out.println("######################################################");
				//System.out.println("#### 销售额： getOrderAmt = " + getOrderAmt);
				String[] keys1 = getOrderAmt.split("\\]\\,\\[");
				for (String key1 : keys1) {
					todayColumn += getFmtPoint(key1.split(",")[5].replaceAll("\\]\\]", "")) + ",";

					String proinceId = key1.split(",")[4].replaceAll("\"", "").split("_")[1];
					provinceName += "\'"+provinceMap.get(proinceId)+"\'" + ",";
					provinceList.add(proinceId);

				}
				todayColumn = todayColumn.substring(0, todayColumn.length() - 1) + "]";
				provinceName = provinceName.substring(0, provinceName.length() - 1) + "]";

				/*****************************************************************************/
				/*****************************************************************************/

				String getOrderNum = drpc.execute("getOrderNum",
						DataUtils.getInputMsg(null, "cf", "ordernum", provinceList));
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				//System.out.println("@@@@ 订单数： getOrderNum = " + getOrderNum);
				String[] keys2 = getOrderNum.split("\\]\\,\\[");
				for (String key2 : keys2) {
					todaySpline += key2.split(",")[5].replaceAll("\\]\\]", "") + ",";
				}
				todaySpline = todaySpline.substring(0, todaySpline.length() - 1) + "]";

				
				/*****************************************************************************/
				/*****************************************************************************/
				String data = "{\'todayColumn\':" + todayColumn +
						",\'todaySpline\':" + todaySpline + 
						",\'provinceName\':" + provinceName + "}";

				System.out.println("data: " + data);

				boolean flag = this.sendData("jsFun", response, data);
				if (!flag) {
					break;
				}
				Utils.sleep(3000);
			}
		} catch (TException e) {
			e.printStackTrace();
		} catch (DRPCExecutionException e) {
			e.printStackTrace();
		}

	}

	public boolean sendData(String jsFun, HttpServletResponse response, String data) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter()
					.write("<script type=\"text/javascript\">parent." + jsFun + "(\"" + data + "\")" + "</script>");
			response.flushBuffer();

			return true;
		} catch (Exception e) {
			System.out.println("HTTP LONG CONNECT OVER BREAK");
			return false;
		}
	}

	public String getFmtPoint(String str) {
		DecimalFormat format = new DecimalFormat("#");
		if (str != null) {
			return format.format(Double.parseDouble(str));
		}
		return null;
	}

}
