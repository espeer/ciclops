<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>

<html>
<head><title>Create new configuration</title></head>
<body>

<h1>Create new configuration</h1>
<br />

<html:form action="/CreateConfiguration">
<table border="0">
<tr>
<td>Type:</td>
<td>
<html:select property="configurationData.type">
<html:options property="types"/>
</html:select>
</td>
</tr>
<tr>
<td>Name:</td>
<td>
<html:text property="configurationData.name" size="100"/> 
</td>
</tr>
<tr>
<td valign="top">Description:</td>
<td>
<html:textarea property="configurationData.configuration" rows="15" cols="100"/>
</td>
</tr>
<tr>
<td></td>
<td>
<html:submit>Submit</html:submit>
</td></tr>
</table>
</html:form>
 
</body>
</html>
