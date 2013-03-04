<?php

require_once 'OpenTokSDK.php';

// You must have a valid sessionId and an OpenTokSDK object
$apiObj = new OpenTokSDK('23144662', '963cee84e34e35a02fe53387824223952807f16e');
$sessionId = '1_MX4yMzE0NDY2Mn4xMjcuMC4wLjF-U3VuIE1hciAwMyAxMTo0NTozNSBQU1QgMjAxM34wLjQ1NDE3Mjczfg';

// After creating a session, call generateToken(). Require parameter: SessionId
$token = $apiObj->generateToken($sessionId);

// Giving the token a moderator role, expire time 5 days from now, and connectionData to pass to other users in the session
$token = $apiObj->generateToken($sessionId, RoleConstants::MODERATOR, time() + (5*24*60*60), "hello world!" );

?>


<!DOCTYPE HTML>
<html>
	<head>
        <title>OpenTok Simple Example</title>
		<script src="http://static.opentok.com/v1.1/js/TB.min.js" type="text/javascript" charset="utf-8"></script>
		<link href="http://static.opentok.com/opentok/assets/css/demos.css" type="text/css" rel="stylesheet" >
        <script type="text/javascript" charset="utf-8">
			TB.addEventListener("exception", exceptionHandler);
			var session = TB.initSession("1_MX4yMzE0NDY2Mn4xMjcuMC4wLjF-U3VuIE1hciAwMyAxMTo0NTozNSBQU1QgMjAxM34wLjQ1NDE3Mjczfg"); // Replace with your own session ID. See https://dashboard.tokbox.com/projects
			session.addEventListener("sessionConnected", sessionConnectedHandler);
			session.addEventListener("streamCreated", streamCreatedHandler);
			session.connect("23144662", '<?= $token ?>'); // Replace with your API key and token. See https://dashboard.tokbox.com/projects
			                                         // and https://dashboard.tokbox.com/projects

			function sessionConnectedHandler(event) {
				 subscribeToStreams(event.streams);
				 session.publish();
			}
			
			function streamCreatedHandler(event) {
				subscribeToStreams(event.streams);
			}
			
			function subscribeToStreams(streams) {
				for (i = 0; i < streams.length; i++) {
					var stream = streams[i];
					if (stream.connection.connectionId != session.connection.connectionId) {
						session.subscribe(stream);
					}
				}
			}
			
			function exceptionHandler(event) {
				alert("Exception: " + event.code + "::" + event.message);
			}
			

		</script>
		<style>
			html, body {
				width: 100%;
				height: 100%;
			}
		</style>
    </head>
    <body>
    		<iframe width="100%" height="50%" src="http://measrme.com/photos/">
    		</iframe>
    </body>
</html>
