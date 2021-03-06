<?php

/**
* OpenTok PHP Library
* http://www.tokbox.com/
*
* Copyright (c) 2011, TokBox, Inc.
* Permission is hereby granted, free of charge, to any person obtaining
* a copy of this software and associated documentation files (the "Software"), 
* to deal in the Software without restriction, including without limitation 
* the rights to use, copy, modify, merge, publish, distribute, sublicense, 
* and/or sell copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included
* in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
* THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
* THE SOFTWARE.
*/

require_once '../OpenTokSDK.php';

// You must have a valid sessionId and an OpenTokSDK object
$apiObj = new OpenTokSDK('23144662', '963cee84e34e35a02fe53387824223952807f16e');
$sessionId = '1_MX4yMzE0NDY2Mn4xMjcuMC4wLjF-U3VuIE1hciAwMyAxMTo0NTozNSBQU1QgMjAxM34wLjQ1NDE3Mjczfg';

// After creating a session, call generateToken(). Require parameter: SessionId
$token = $apiObj->generateToken($sessionId);

// Giving the token a moderator role, expire time 5 days from now, and connectionData to pass to other users in the session
$token = $apiObj->generateToken($sessionId, RoleConstants::MODERATOR, time() + (5*24*60*60), "hello world!" );
echo $token;


?>
