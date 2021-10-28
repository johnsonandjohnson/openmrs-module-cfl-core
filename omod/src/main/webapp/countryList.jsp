<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

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
