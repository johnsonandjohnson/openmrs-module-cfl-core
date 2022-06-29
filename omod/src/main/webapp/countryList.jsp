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

<h2><spring:message code="cfl.manageCountries.title"/></h2>

<a href="countryForm.form">Add new country</a>
<a href="countryImport.form">Import countries</a>

<br/>
<br/>

<b class="boxHeader">Current countries</b>
<form method="post">
    <fieldset>
        <table>
            <c:forEach var="entry" items="${model.conceptMap}">
                <tr>
                    <td valign="top">
                        <a href="/openmrs/dictionary/concept.htm?conceptId=${entry.key}">
                            <c:out value="${entry.value}"/>
                        </a>
                    </td>
                    <td>
                        <span>
                            <a href="editCountryForm.form?conceptId=${entry.key}">Edit</a>
                        </span>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br/>
    </fieldset>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
