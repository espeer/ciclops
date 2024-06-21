<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>

<html>
<head><title>Create Benchmark</title></head>
<body>

<h1>Create Benchmark</h1>
<br />

<html:form action="/CreateBenchmark">
<table>
<tr><td>
Name: 
</td><td>
<html:text property="benchmarkData.name" size="100"/>
</td></tr><tr><td>
Time Measurement:
</td><td>
<html:select property="benchmarkData.timeMeasurementKey">
<html:options collection="measurements" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td>
Error Measurement:
</td><td>
<html:select property="benchmarkData.errorMeasurementKey">
<html:options collection="measurements" property="primaryKey" labelProperty="name"/>
</html:select>
</td></tr><tr><td>
Samples:
</td><td>
<html:text property="benchmarkData.samples" size="5"/>
</td></tr><tr><td>
Resolution:
</td><td>
<html:text property="benchmarkData.resolution" size="5"/> 
</td></tr><tr><td valign="top">
Progress Indicators:
</td><td>
<html:select multiple="true" property="progressIndicators" size="3">
<html:options collection="progressIndicators" property="primaryKey" labelProperty="name"/>
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
