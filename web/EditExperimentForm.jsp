<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>

<html>
<head><title>Edit Experiment</title></head>
<body>

<h1>Edit Experiment</h1>
<br />

<html:form action="/EditExperiment">
<table>
<tr><td>
Name: 
</td><td>
<html:text property="experimentData.name" size="100"/>
</td></tr><tr><td>
Algorithm:
</td><td>
<html:select property="experimentData.algorithmKey">
<html:options collection="algorithms" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td>
Samples:
</td><td>
<html:text property="experimentData.samples" size="5"/>
</td></tr><tr><td>
Resolution:
</td><td>
<html:text property="experimentData.resolution" size="5"/> 
</td></tr><tr><td valign="top">
Progress Indicators:
</td><td>
<html:select multiple="true" property="progressIndicators" size="3">
<html:options collection="progressIndicators" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td valign="top">
Measurements:
</td><td>
<html:select multiple="true" property="measurements" size="3">
<html:options collection="measurements" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td valign="top">
Problems:
</td><td>
<html:select multiple="true" property="problems" size="10">
<html:options collection="problems" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td>
<html:submit>Submit</html:submit>
</td><td><td></tr>
</table>
</html:form>
 
</body>
</html>
