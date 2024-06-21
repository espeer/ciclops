<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Experiments</title></head>
<body>

<html:link page="/CreateExperimentForm.do">[Create new experiment]</html:link>
<p />
<table border="1">
  <logic:iterate id="item" name="experiments">
    
    <tr><td>
    Name: <bean:write name="item" property="name"/>
    <br /><br />
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/EditExperimentForm.do">[Edit]</html:link>
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/RemoveExperiment.do">[Remove]</html:link>
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/ScheduleExperiment.do">[Schedule]</html:link>

    </td><tr>
  </logic:iterate>
</table>

</body>
</html>
