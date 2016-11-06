<html>
<head>
<title>Porto</title>
<script type="text/javascript"
	src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="//code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<script type="text/javascript"
	src="http://code.highcharts.com/stock/5.0.2/highstock.js"></script>
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

		function onTestHighstocks(config,symbol) {
		    //var url = '/rest/v1/test?callback=?';
		    var url = '/rest/v1/stock/' + symbol + '?callback=?';
			$.getJSON(url, function(ohlc) {
			   data = createData(ohlc);
               createChart(config, symbol, data);
			})
		}

        function createData(data) {
          // split the data set into ohlc and volume
          var ohlc = [],
              volume = [],
              dataLength = data.length,
              i = 0;

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
          return { ohlc: ohlc, volume: volume};
        }

		function createChart(config, symbol, data) {
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
          config.series[0].data = data.ohlc;
          config.series[0].name = symbol;
          config.series[1].data = data.volume;
          $('#container').highcharts('StockChart', config);
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

		function onGraph(symbol) {
		   $.getJSON('/rest/v1/config/highcharts', function(config) {
              config.title.text = symbol;
              onTestHighstocks(config, symbol);
           })
		}

		function onOptionChange(e) {
		    onGraph(e.target.value);
        }

        function setupMenu() {
            $.getJSON('/rest/v1/stocks', function(stocks) {
                          for (stock of stocks) {
              		        $("#Menu").append($('<option>').text(stock.label)
              		                                       .attr('value', stock.symbol));
                          }
                          $("#Menu").change(onOptionChange);
                       })
        }

		$(document).ready(setupMenu)
	</script>

	<div id="container" style="height: 600px; min-width: 310px"></div>
	<form id="MenuForm"><select id="Menu"/></form>
</body>
</html>
