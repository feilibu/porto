<html>
  <head>
    <title>jQuery Hello World</title>
    <script type="text/javascript" src="//code.jquery.com/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src=" //code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.1/themes/black-tie/jquery-ui.css"></script>
    <style>
      .ui-menu {
         width: 200px;
      }
  </style>
  </head>
 
  <body>
 
  <script type="text/javascript">
    $(document).ready(function(){
     $( "#Menu" ).menu( 
      { 
         select: function(event,ui) 
	         {
		    if(ui.item[0].id == 'populate') 
		      { alert( 'Coucou:' ) }
		    else if(ui.item[0].id == 'graph') 
		      { alert( 'Kiki' ) }
		 }
       } )
      })
  </script>

  <ul id="Menu">
    <li id="populate">Populate</li>
    <li id="graph">Graph</li>
  </ul>
  </body>
</html>
