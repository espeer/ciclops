<%@page contentType="text/html"%>
<html>
<head><title>Error page</title></head>
<body>

<%-- <jsp:useBean id="exception" scope="request" class="java.lang.Exception" /> --%>
<%-- <jsp:getProperty name="exception"  property="message" /> --%>

Error: <%= request.getAttribute("message") %>

</body>
</html>
