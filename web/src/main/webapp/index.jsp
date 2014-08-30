<html>
<head>
<title>jQuery Hello World</title>
<script type="text/javascript"
	src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src=" //code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.11.1/themes/black-tie/jquery-ui.css">
<style>
.ui-menu {
	width: 200px;
}
</style>
</head>

<body>
	<script type="text/javascript">
		function onTestSuccess(data) {
			alert("toto;" + data.toto + ";titi;" + data.titi)
		}

		function onTest() {
			$.ajax("/web/rest/v1/test").done(function(data) {
				onTestSuccess(data)
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
			alert('graph')
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
</body>
</html>
