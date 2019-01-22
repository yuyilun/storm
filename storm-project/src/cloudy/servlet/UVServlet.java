package cloudy.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;

import cloudy.hbase.dao.HBaseDao;
import cloudy.hbase.dao.imp.HBaseDaoImp;
import cloudy.tools.CommonUtils;
import cloudy.tools.DateFmt;
/**
 * 
 * 这个例子中有几个BUG
 * 1、每次重启后，实时计数从0开始 ----- 使用事务
 * 2、细节优化
 * 
 * 实际生产中，使用jstorm
 * http://www.jstorm.io/
 * 
 */
@WebServlet("/UVServlet")
public class UVServlet extends HttpServlet {

	/**
	 */
	private static final long serialVersionUID = 1L;
	private HBaseDao dao;
	private String today = null;
	String[] colsArr = new String[] { "time_title", "x_value", "uv" };

	public String initData = "[]";
	public String initHisData = "[]";
	public String initHourData = "[]";

	private String jsonStr = "";

	private String xValue = "0";
	private String xTitle = "0";
	private String uv = "0";

	@Override
	public void init() throws ServletException {
		dao = new HBaseDaoImp();
		/** 跨天参数 */
		today = DateFmt.getCountDate(null, DateFmt.date_short);
		/** 每次刷新页面更新数据 */
		initStrData();
	}

	private void initStrData() {

		String rowkey_today = today.replaceAll("-", "");// 今天
		String rowkey_his = DateFmt.getCountDate(null, DateFmt.date_short, -1).replaceAll("-", "");// 昨天

		/** 读取线图当天数据 */
		List<Result> listToday = dao.getRows("uv_table", rowkey_today, colsArr);
		initData = CommonUtils.transformHistoryData(listToday, colsArr);

		/** 读取上月同比数据 */
		List<Result> listYc = dao.getRows("uv_table", rowkey_his, colsArr);
		initHisData = CommonUtils.transformHistoryData(listYc, colsArr);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			
			
			req.setCharacterEncoding("utf-8");
			resp.setContentType("text/html;charset=utf-8");

			initHourData = getHourUVData(today + "_hour", dao);// 获取今天的hour历史数据

			initStrData(); // 获取初始化数据

			jsonStr = "{\'initData\':" + initData + ",\'initHisData\':" + initHisData + ",\'initHourData\':'" + initHourData
					+ "'}";

			resp.getWriter().write("<script type=\"text/javascript\">parent.msg" + "(\"" + jsonStr + "\")</script>");
			resp.flushBuffer();

			while (true) {

				// 每次循环追加一个点
				// 判断是否跨天
				if (!DateFmt.getCountDate(null, DateFmt.date_short).equals(today)) {
					init();
					jsonStr = "{\'initData\':" + initData + ",\'initHisData\':"
							+ initHisData + ",\'isNewDay\':" + 1 + ",\'initHourData\':'"
							+ initHourData + "'}";

					resp.setContentType("text/html;charset=utf-8");
					resp.getWriter().write(
							"<script type=\"text/javascript\">parent.msg"
									+ "(\"" + jsonStr + "\")</script>");
					resp.flushBuffer();
				}

				/** 读取hbase数据 */
				String[] valueArr = CommonUtils.findLineData(dao, "uv_table", today + "_lastest", colsArr);
				if (valueArr != null) {
					xTitle = valueArr[0];
					xValue = valueArr[1];
					uv = valueArr[2];
				}

				jsonStr = "{\'name\':\'" + xTitle + "\',\'x\':" + xValue + ",\'y\':" + uv + ",\'initHourData\':'"
						+ initHourData + "'}";

				/** 判断一下如果值不为null才sendMsg */
				if (uv != null) {
					boolean b = sendMsg(jsonStr, resp, "msg");
					if (b) {// 页面已被关闭，加点异常，此方法结束
						break;
					}
				}
				// 5秒推送一次数据
				Thread.sleep(5000);
				initHourData = this.getHourUVData(today+"_hour", dao);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
	}

	// 以下方法的意思是将msg打到前台页面调用前台的“function msg(m)”方法进行显示
	protected boolean sendMsg(String msg, HttpServletResponse response, String javascriptMethod) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(
					"<script type=\"text/javascript\">parent." + javascriptMethod + "(\"" + msg + "\")</script>");
			response.flushBuffer();

		} catch (Exception e) {
			System.out.println("svlt long connect time error.....");
			return true;
		}
		return false;
	}

	/**
	 * 获取UV数据
	 */
	@SuppressWarnings("deprecation")
	public String getHourUVData(String date, HBaseDao dao) {
		List<Result> list = dao.getRows("uv_table", date);
		String result = "[";
		String[] datas = new String[24];
		for (Result rs : list) {
			for (KeyValue keyValue : rs.raw()) {
				String hourId = new String(keyValue.getRow()).split("_")[2];
				//results
				if ("uv".equals(new String(keyValue.getQualifier()))) {
					datas[Integer.parseInt(hourId)] = new String(keyValue.getValue());
					break;
				}
			}
		}
		
		for(String data : datas) {
			if(data == null) {
				data = "0";
			}
			result +=  data + ",";
		}
		result = result.substring(0, result.length() -1 ) +"]" ;
		return result;
	}

}
