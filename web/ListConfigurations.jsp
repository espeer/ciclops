<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Configurations</title></head>
<body>

<html:form action="/ListConfigurations">
Type: &nbsp;
<html:select property="type">
<html:options property="types"/>
</html:select>
<html:submit>Go</html:submit>
</html:form>
<p />
<html:link paramId="type" paramName="ListConfigurationsForm" paramProperty="type" page="/CreateConfigurationForm.do">[Create new configuration]</html:link>
<p />
<table border="1">
  <logic:iterate id="item" name="ListConfigurationsForm" property="configurationItems">
    
    <tr><td>
    <bean:write name="item" property="configurationData.name"/><br /><br />
    <html:textarea readonly="true" cols="100" rows="15" name="item" property="configurationData.configuration"/>
    <br />
    <html:link name="item" property="requestParameters" page="/EditConfigurationForm.do">[Edit]</html:link>
    <html:link name="item" property="requestParameters" page="/RemoveConfiguration.do">[Remove]</html:link>
    <br /><br />
    </td><tr>
  </logic:iterate>
  </table>

</body>
</html>
