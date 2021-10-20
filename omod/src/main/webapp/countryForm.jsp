<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<h2><spring:message code="cfl.addNewCountry.title"/></h2>

<form method="post">
    <fieldset>
        <table>
            <tr>
                <td>Country name<span class="required">*</span></td>
                <td>
                    <spring:bind path="model.name">
                        <input required type="text" name="${status.expression}" value="${status.value}" size="20" />
                        <c:if test="${status.errorMessage != ''}">
                            <c:if test="${status.errorMessage != ''}">
                                <span class="error">${status.errorMessage}</span>
                            </c:if>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
        </table>
        </br>
        <input type="submit" value="Save" name="save">
    </fieldset>
</form>

<br/>
<a href="countryList.form">Back</a>

<%@ include file="/WEB-INF/template/footer.jsp" %>
