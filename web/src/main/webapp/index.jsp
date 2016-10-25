<html>
<head>
<title>Porto</title>
<script type="text/javascript"
	src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src=" //code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<script type="text/javascript"
	src="http://code.highcharts.com/stock/highstock.js"></script>
<script type="text/javascript"
	src="http://code.highcharts.com/stock/modules/exporting.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.11.1/themes/black-tie/jquery-ui.css" />


<style>
.ui-menu {
	width: 200px;
}
</style>
</head>

<body>
	<script type="text/javascript">
		function onTestSuccess(data) {
			alert("toto")
		}

		function onTestHighstocks() {
			$.getJSON('/web/rest/v1/test?callback=?', function(data) {
				createChart(data)
			})
		}

		function createChart(data) {
			// Create the chart
			window.chart = new Highcharts.StockChart({
				chart : {
					renderTo : 'container'
				},

				rangeSelector : {
					selected : 1
				},

				title : {
					text : 'Afei Stock Price'
				},

				series : [ {
					name : 'AAPL',
					data : data,
					tooltip : {
						valueDecimals : 2
					}
				} ]
			})
		}

		function onTestFailure(jqXHR, status, error) {
			alert("Error in test;" + status + ";" + error)
		}

		function onTest() {
			$.ajax("/web/rest/v1/test").done(function(data) {
				onTestSuccess(data)
			}).fail(function(jqXHR, status, error) {
				onTestFailure(jqXHR, status, error)
			})
		}

		function onPopulateSuccess(data) {
			alert(data)
		}

		function onPopulate() {
			$.ajax("/web/rest/v1/populate").done(function(data) {
				onPopulateSuccess(data)
			})
		}

		function onGraph() {
			onTestHighstocks()
		}

		function onSelect(event, ui) {
			if (ui.item[0].id == 'populate') {
				onPopulate()
			} else if (ui.item[0].id == 'graph') {
				onGraph()
			} else if (ui.item[0].id == 'test') {
				onTest()
			}
		}

		$(document).ready(function() {
			$("#Menu").menu({
				select : function(event, ui) {
					onSelect(event, ui)
				}
			})
		})
	</script>

	<ul id="Menu">
		<li id="test">Test</li>
		<li id="populate">Populate</li>
		<li id="graph">Graph</li>
	</ul>
	<div id="container" style="height: 400px; min-width: 310px"></div>
</body>
</html>
