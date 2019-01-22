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
import org.apache.hadoop.hbase.util.Bytes;

import backtype.storm.utils.Utils;
import cloudy.hbase.dao.HBaseDao;
import cloudy.hbase.dao.imp.HBaseDaoImp;
import cloudy.tools.DateFmt;
import cloudy.vo.AreaVo;

/**
 * Servlet implementation class AreaServlet
 */
@WebServlet("/AreaServlet")
public class AreaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HBaseDao hBaseDao = null;
	String today = null;
	String hisDay = null;
	String hisData = null;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AreaServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		hBaseDao = new HBaseDaoImp();
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
		hisDay = DateFmt.getCountDate(null, DateFmt.date_short,-1);
		hisData = this.getData(hisDay, hBaseDao);
		
		while (true) {
			today = DateFmt.getCountDate(null, DateFmt.date_short);
			// select hbase per 3 second
			String data = this.getData(today, hBaseDao);
			data = "{\'todayData\':" + data +",hisData:"+ hisData+"}";
			boolean flag = this.sendData("jsFun", response, data);
			if (!flag) {
				break;
			}

			Utils.sleep(3000);
		}
	}

	@SuppressWarnings("deprecation")
	public String getData(String date, HBaseDao hBaseDao) {

		List<Result> list = hBaseDao.getRows("area_order", date);
		AreaVo vo = new AreaVo();

		for (Result rs : list) {
			String rowKey = Bytes.toString(rs.getRow());
			String areaId = null;

			if (rowKey.split("_").length == 2) {
				areaId = rowKey.split("_")[1];
			}

			for (KeyValue kv : rs.raw()) {
				if ("order_amt".equals(Bytes.toString(kv.getQualifier()))) {
					vo.setData(areaId, Bytes.toString(kv.getValue()));
					break;
				}
			}
		}

		return vo.toString();
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

}
