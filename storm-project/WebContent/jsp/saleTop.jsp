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
	var series3;
	
	function jsFun(m) {
		var jsData = eval("("+m+")");
		series1.setData(eval(jsData.todayColumn));
		series2.setData(eval(jsData.todaySpline));
		var chart = $('#container').highcharts();
		chart.xAxis[0].setCategories(eval(jsData.provinceName));
	}
	
	function init(){
		var action = "<%=path%>/SaleTopServlet";
		$("#myform").attr("action", action);
		$("#myform").submit();
	}
</script>
<script type="text/javascript">
var chart = Highcharts.chart('container', {
	chart: {
		zoomType: 'xy',
		events:{
			load:function(){
				series1 = this.series[0];
				series2 = this.series[1];
				init();
			}
		}
	},
	title: {
		text: '城市销售额排行'
	},
	subtitle: {
		text: 'Top5'
	},
	xAxis: [{
		categories: [],
		crosshair: true
	}],
	yAxis: [{ // Primary yAxis
		labels: {
			format: '{value}个',
			style: {
				color: Highcharts.getOptions().colors[1]
			}
		},
		title: {
			text: '订单数',
			style: {
				color: Highcharts.getOptions().colors[1]
			}
		}
	}, { // Secondary yAxis
		title: {
			text: '交易金额',
			style: {
				color: Highcharts.getOptions().colors[0]
			}
		},
		labels: {
			format: '{value} 元',
			style: {
				color: Highcharts.getOptions().colors[0]
			}
		},
		opposite: true
	}],
	tooltip: {
		shared: true
	},plotOptions: {
        column : {
        	dataLabels : {
        		enabled : true
        	}
        },
        spline : {
        	dataLabels : {
        		enabled : true
        	}
        }
    },
	legend: {
		layout: 'vertical',
		align: 'left',
		x: 120,
		verticalAlign: 'top',
		y: 100,
		floating: true,
		backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
	},
	series: [{
		name: '交易金额',
		type: 'column',
		yAxis: 1,
		data: [],
		tooltip: {
			valueSuffix: ' 元'
		}
	}, {
		name: '订单数',
		type: 'spline',
		data: [],
		tooltip: {
			valueSuffix: ' 个'
		}
	}]
});
</script>

</html>