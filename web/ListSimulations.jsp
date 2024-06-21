<%@page contentType="text/html"%>
<%@taglib uri="WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
<head><title>List Result Sets</title></head>
<body>

<p />
<table border="1">
  <logic:iterate id="item" name="simulations">
    
    <tr><td>
    Algorithm: <bean:write name="item" property="algorithm"/><br />
    Problem: <bean:write name="item" property="problem"/><br />
    Samples: <bean:write name="item" property="samples"/><br />
    Samples Completed: <bean:write name="item" property="samplesCompleted" /><br />
    Resolution: <bean:write name="item" property="resolution"/><br />
    Running: <bean:write name="item" property="running"/><br />
	Stale: <bean:write name="item" property="stale"/><br/>
    <br />
    Measurements:<br />
    <logic:iterate id="tmp" name="item" property="resultSets">

        <html:link paramId="key" paramName="tmp" paramProperty="primaryKey" page="/ListResults.do">
          <bean:write name="tmp" property="measurement"/>
        </html:link>&nbsp;
        <html:link paramId="key" paramName="tmp" paramProperty="primaryKey" page="/ListNormalTestResults.do">
          [Normal Test]
        </html:link>
        <br />
    </logic:iterate>
    <br /><br />
    Progress Indicators:<br />
    <logic:iterate id="tmp" name="item" property="progressIndicators">
      <bean:write name="tmp"/>
    </logic:iterate>

    <br /><br />
    Experiments:<br />
    <logic:iterate id="tmp" name="item" property="experiments">
        <bean:write name="tmp"/>
    </logic:iterate>
    <br /><br />
    Benchmarks:<br />    
    <logic:iterate id="tmp" name="item" property="benchmarks">
        <bean:write name="tmp"/>
    </logic:iterate>


    <br />
    <html:link paramId="key" paramName="item" paramProperty="primaryKey" page="/RemoveSimulation.do">[Remove]</html:link>
    
    </td><tr>
  </logic:iterate>
</table>

</body>
</html>
