<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<script type="text/javascript">

</script>

<h2><spring:message code="cfl.uploadCountryList.title"/></h2>

<form method="POST" enctype="multipart/form-data">
    File to import: <input type="file" name="file" />
    <br/><br/>
    <input type="submit"/>
</form>

<br/>
<a href="countryList.form">Back</a>

<%@ include file="/WEB-INF/template/footer.jsp" %>
