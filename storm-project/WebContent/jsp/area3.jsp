<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>区域实时金额</title>
<script type="text/javascript" src="../js/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="../js/highcharts/highcharts.js"></script>
<script type="text/javascript"
	src="../js/highcharts/modules/exporting.js"></script>
<script type="text/javascript"
	src="../js/highcharts/highcharts-plugins/highcharts-zh_CN.js"></script>
</head>
<body>

	<form action="" method="post" target="myiframe" id="myform"></form>

	<iframe id="myiframe" name="myiframe" style="display: none"> </iframe>


	<div id="container" style="min-width:400px;height:400px"></div>

</body>
<script type="text/javascript">
	var series1;
	var series2;
	
	function jsFun(m) {
		var jsData = eval("("+m+")");
		series1.setData(eval(jsData.todayData));
		series2.setData(eval(jsData.hisData));
	}
	
	function init(){
		var action = "<%=path%>/AreaServlet";
		$("#myform").attr("action", action);
		$("#myform").submit();
	}
</script>
<script type="text/javascript">
var chart = Highcharts.chart('container',{
	chart: {
		type: 'column',
		events:{
			load:function(){
				series1 = this.series[0];
				series2 = this.series[1];
				init();
			}
		}
	},
	title: {
		text: '区域实时金额'
	},
	subtitle: {
		text: '按天统计'
	},
	xAxis: {
		categories: [
			'北京', '上海', '广州', '深圳', '成都'
		],
		crosshair: true
	},
	yAxis: {
		min: 0,
		title: {
			text: '交易金额'
		}
	},
	tooltip: {
		// head + 每个 point + footer 拼接成完整的 table
		headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		'<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
		footerFormat: '</table>',
		shared: true,
		useHTML: true
	},
	plotOptions: {
		column: {
			borderWidth: 0
		}
	},
	series: [{
		name: '今天',
		data: [49.9, 71.5, 106.4, 129.2, 144.0]
	}, {
		name: '昨天',
		data: [83.6, 78.8, 98.5, 93.4, 106.0]
	}]
});
</script>

</html>