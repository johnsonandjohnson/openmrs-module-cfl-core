<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<%--
  ~ This Source Code Form is subject to the terms of the Mozilla Public License,
  ~ v. 2.0. If a copy of the MPL was not distributed with this file, You can
  ~ obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
  ~ the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
  ~ <p>
  ~ Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
  ~ graphic logo is a trademark of OpenMRS Inc.
  --%>

<style type="text/css">
	#tooltip {
	    font-style: italic;
	}
</style>

<h2><spring:message code="cfl.uploadCountryList.title"/></h2>

<p id="tooltip">
<spring:message code="cfl.importCountries.tooltip" htmlEscape="false" />
</p>

<form method="POST" enctype="multipart/form-data">
    <spring:message code="cfl.importCountries.fileToImport.label"/> <input type="file" name="file"/>
    <br/><br/>
    <input type="submit" value="<spring:message code="common.save"/>" />
</form>

<br/>
<a href="countryList.form"><spring:message code="common.back"/></a>

<%@ include file="/WEB-INF/template/footer.jsp" %>
