<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>Active Tasks</title></head>
<body>

<h1>Active Tasks</h1>

<p />
<table border="0">
  <logic:iterate id="item" name="tasks">
    
    <tr><td>
    <bean:write name="item" property="accessTime"/>
    </td>
    <td>
    <bean:write name="item" property="client"/>
    </td>
    <td>
    <bean:write name="item" property="progress"/>
    </td></tr>
  </logic:iterate>
</table>

</body>
</html>
