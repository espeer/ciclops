<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>

<html>
<head><title>Edit configuration</title></head>
<body>

<h1>Edit configuration</h1>
<br />

<html:form action="/EditConfiguration">
<table border="0">
<tr>
<td>Type:</td>
<td>
<html:text readonly="true" property="configurationData.type" size="20"/>
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
<%--
<html:hidden property="configurationData.primaryKey"/>
--%>
</html:form>
 
</body>
</html>
