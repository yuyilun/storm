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
<script type="text/javascript" src="../js/highcharts/highcharts-3d.js"></script>
<script type="text/javascript"
	src="../js/highcharts/modules/exporting.js"></script>
<script type="text/javascript"
	src="../js/highcharts/themes/grid-light.js"></script>
<script type="text/javascript"
	src="../js/highcharts/highcharts-plugins/highcharts-zh_CN.js"></script>
</head>
<body>

	<form action="" method="post" target="myiframe" id="myform"></form>

	<iframe id="myiframe" name="myiframe" style="display: none"> </iframe>


	<div id="container"></div>
	<div id="sliders">
		<table>
			<tr>
				<td>α 角（内旋转角）</td>
				<td><input id="alpha" type="range" min="0" max="45" value="15" />
					<span id="alpha-value" class="value"></span></td>
			</tr>
			<tr>
				<td>β 角（外旋转角）</td>
				<td><input id="beta" type="range" min="-45" max="45" value="15" />
					<span id="beta-value" class="value"></span></td>
			</tr>
			<tr>
				<td>深度</td>
				<td><input id="depth" type="range" min="20" max="100"
					value="50" /> <span id="depth-value" class="value"></span></td>
			</tr>
		</table>
	</div>

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
	var chart = new Highcharts.Chart({
		chart : {
			renderTo : 'container',
			type : 'column',
			options3d : {
				enabled : true,
				alpha : 15,
				beta : 15,
				depth : 50,
				viewDistance : 25
			},
			events:{
				load:function(){
					series1 = this.series[0];
					series2 = this.series[1];
					init();
				}
			}
		},
		title : {
			text : '区域实时金额'
		},
		subtitle : {
			text : '按天统计'
		},
		plotOptions : {
			column : {
				depth : 25,
				dataLabels:{
					enabled:true
				}
			}
		},
		xAxis : {
			categories : [ '北京', '上海', '广州', '深圳', '成都' ],
			title : {
				enabled : true,
				text : '地区',
			}
		},
		yAxis : {
			min : 0,
			labels : {},
			title : {
				enabled : true,
				text : '交易金额',
			}
		},
		series : [ {
			name : '当前',
			color : 'blue',
			data : []
		}, {
			name : '上周同期',
			color : '#00B050',
			data : []
		} ]
	});
	// 将当前角度信息同步到 DOM 中
	var alphaValue = document.getElementById('alpha-value'), betaValue = document
			.getElementById('beta-value'), depthValue = document
			.getElementById('depth-value');
	function showValues() {
		alphaValue.innerHTML = chart.options.chart.options3d.alpha;
		betaValue.innerHTML = chart.options.chart.options3d.beta;
		depthValue.innerHTML = chart.options.chart.options3d.depth;
	}
	// 监听 sliders 的变化并更新图表
	$('#sliders input').on('input change', function() {
		chart.options.chart.options3d[this.id] = this.value;
		showValues();
		chart.redraw(false);
	});
	showValues();
</script>

</html>