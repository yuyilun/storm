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
	src="../js/highcharts/modules/oldie.js"></script>
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
var chart = Highcharts.chart('container', {
	chart: {
		type: 'bar',
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
		categories: ['北京', '上海', '广州', '深圳', '成都'],
		title: {
			text: null
		}
	},
	yAxis: {
		min: 0,
		title: {
			text: '交易金融',
			align: 'high'
		},
		labels: {
			overflow: 'justify'
		}
	},
	tooltip: {
		valueSuffix: ''
	},
	plotOptions: {
		bar: {
			dataLabels: {
				enabled: true,
				allowOverlap: true // 允许数据标签重叠
			}
		}
	},
	legend: {
		layout: 'vertical',
		align: 'right',
		verticalAlign: 'top',
		x: -40,
		y: 100,
		floating: true,
		borderWidth: 1,
		backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
		shadow: true
	},
	series: [{
		name: '昨天',
		data: [107, 31, 635, 203, 2]
	}, {
		name: '今天',
		data: [973, 914, 4054, 732, 34]
	}]
});
</script>

</html>