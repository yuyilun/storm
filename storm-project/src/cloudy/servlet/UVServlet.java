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
 * ����������м���BUG
 * 1��ÿ��������ʵʱ������0��ʼ ----- ʹ������
 * 2��ϸ���Ż�
 * 
 * ʵ�������У�ʹ��jstorm
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
		/** ������� */
		today = DateFmt.getCountDate(null, DateFmt.date_short);
		/** ÿ��ˢ��ҳ��������� */
		initStrData();
	}

	private void initStrData() {

		String rowkey_today = today.replaceAll("-", "");// ����
		String rowkey_his = DateFmt.getCountDate(null, DateFmt.date_short, -1).replaceAll("-", "");// ����

		/** ��ȡ��ͼ�������� */
		List<Result> listToday = dao.getRows("uv_table", rowkey_today, colsArr);
		initData = CommonUtils.transformHistoryData(listToday, colsArr);

		/** ��ȡ����ͬ������ */
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

			initHourData = getHourUVData(today + "_hour", dao);// ��ȡ�����hour��ʷ����

			initStrData(); // ��ȡ��ʼ������

			jsonStr = "{\'initData\':" + initData + ",\'initHisData\':" + initHisData + ",\'initHourData\':'" + initHourData
					+ "'}";

			resp.getWriter().write("<script type=\"text/javascript\">parent.msg" + "(\"" + jsonStr + "\")</script>");
			resp.flushBuffer();

			while (true) {

				// ÿ��ѭ��׷��һ����
				// �ж��Ƿ����
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

				/** ��ȡhbase���� */
				String[] valueArr = CommonUtils.findLineData(dao, "uv_table", today + "_lastest", colsArr);
				if (valueArr != null) {
					xTitle = valueArr[0];
					xValue = valueArr[1];
					uv = valueArr[2];
				}

				jsonStr = "{\'name\':\'" + xTitle + "\',\'x\':" + xValue + ",\'y\':" + uv + ",\'initHourData\':'"
						+ initHourData + "'}";

				/** �ж�һ�����ֵ��Ϊnull��sendMsg */
				if (uv != null) {
					boolean b = sendMsg(jsonStr, resp, "msg");
					if (b) {// ҳ���ѱ��رգ��ӵ��쳣���˷�������
						break;
					}
				}
				// 5������һ������
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

	// ���·�������˼�ǽ�msg��ǰ̨ҳ�����ǰ̨�ġ�function msg(m)������������ʾ
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
	 * ��ȡUV����
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
