<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/default.css"/>
</head>
<body>
<img src="img/<%= request.getAttribute("status") %>.png" class="align-left"/>
<div>
  <h2>ExternalCacheUpdater</h2>
  <p>Updates all ExternalDataCache objects in Polopoly.</p>
  <p><a href="/workoliday/updateExternalCache?action=start">Start</a> | <a href="/workoliday/updateExternalCache?action=stop">Stop</a></p>
</div>
</body>
</html>