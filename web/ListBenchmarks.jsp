<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Benchmarks</title></head>
<body>

<html:link page="/CreateBenchmarkForm.do">[Create new benchmark]</html:link>
<p />
<table border="1">
  <logic:iterate id="item" name="benchmarks">
    
    <tr><td>
    Name: <bean:write name="item" property="name"/><br />
    <br />
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/EditBenchmarkForm.do">[Edit]</html:link>
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/RemoveBenchmark.do">[Remove]</html:link>
    
    </td><tr>
  </logic:iterate>
</table>

</body>
</html>
