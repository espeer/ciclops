<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Result Sets</title></head>
<body>

<p />
<table border="1">
  <logic:iterate id="row" name="resultMatrix">
    
    <tr>
        <logic:iterate id="col" name="row">
          <td>
            <bean:write name="col" /><br />
          </td>
        </logic:iterate>
    <tr>
  </logic:iterate>
</table>

</body>
</html>
