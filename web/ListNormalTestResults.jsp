<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Normal Test Results</title></head>
<body>

<p />
<table border="1">
  <logic:iterate id="item" name="results">
    
    <tr>
          <td>
            <bean:write name="item" />
          </td>
    <tr>
  </logic:iterate>
</table>

</body>
</html>
