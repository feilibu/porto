<html>
<head>
<title>Porto</title>
<script type="text/javascript"
	src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="//code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
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
                        console.log("1")
			$.getJSON('/rest/v1/test?callback=?', function(ohlc) {
                           console.log("2")
			   data = createData(ohlc);
                           console.log("size(data) = " + data.length);
                           createChart(data);
			})
		}


                function createData(data) {
                  console.log("coucou")
                  // split the data set into ohlc and volume
                  var ohlc = [],
                      volume = [],
                      dataLength = data.length,
                      i = 0;
          
                  console.log("starting");
                  for (i; i < dataLength; i += 1) {
                      ohlc.push([
                          data[i][0], // the date
                          data[i][1], // open
                          data[i][2], // high
                          data[i][3], // low
                          data[i][4] // close
                      ]);
          
                      volume.push([
                          data[i][0], // the date
                          data[i][5] // the volume
                      ]);
                  }
                  console.log("ending");
                  console.log("ohlc size = " + ohlc.length)
                  console.log("volume size = " + volume.length)
                  console.log("datalength" + dataLength)
                  return { ohlc: ohlc, volume: volume};
                }


		function createChart(data) {
                  // create the chart
                  // set the allowed units for data grouping
                  var groupingUnits = [[
                          'week',                         // unit name
                          [1]                             // allowed multiples
                      ], [
                          'month',
                          [1, 2, 3, 4, 6]
                      ]];
                  var ohlc = data.ohlc;
                  var volume = data.volume;
                  console.log("ohlc size = " + ohlc.length)
                  console.log("volume size = " + volume.length)
          
                  $('#container').highcharts('StockChart', {
          
                      rangeSelector: {
                          selected: 1
                      },
          
                      title: {
                          text: 'AAPL Historical'
                      },
          
                      yAxis: [{
                          labels: {
                              align: 'right',
                              x: -3
                          },
                          title: {
                              text: 'OHLC'
                          },
                          height: '60%',
                          lineWidth: 2
                      }, {
                          labels: {
                              align: 'right',
                              x: -3
                          },
                          title: {
                              text: 'Volume'
                          },
                          top: '65%',
                          height: '35%',
                          offset: 0,
                          lineWidth: 2
                      }],
          
                      series: [{
                          type: 'candlestick',
                          name: 'AAPL',
                          data: ohlc,
                          dataGrouping: {
                              units: groupingUnits
                          }
                      }, {
                          type: 'column',
                          name: 'Volume',
                          data: volume,
                          yAxis: 1,
                          dataGrouping: {
                              units: groupingUnits
                          }
                      }]
                  });
                }



		function onTestFailure(jqXHR, status, error) {
			alert("Error in test;" + status + ";" + error)
		}

		function onTest() {
			$.ajax("/rest/v1/test").done(function(data) {
				onTestSuccess(data)
			}).fail(function(jqXHR, status, error) {
				onTestFailure(jqXHR, status, error)
			})
		}

		function onPopulateSuccess(data) {
			alert(data)
		}

		function onPopulate() {
			$.ajax("/rest/v1/populate").done(function(data) {
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
