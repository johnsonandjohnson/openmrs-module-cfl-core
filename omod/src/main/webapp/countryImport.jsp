<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<style type="text/css">
	#tooltip {
	    font-style: italic;
	}
</style>

<h2><spring:message code="cfl.uploadCountryList.title"/></h2>

<p id="tooltip">
Valid file extensions: .txt or .csv </br>
Each country and related cluster members must be separated by a colon. Cluster members must be separated by a comma. </br>
Definition of each country must be in separate line (without any delimiter) </br>
Example: </br>
US: Alabama, Texas, Nevada </br>
India: Asam, Goa, Kerala </br>
</p>

<form method="POST" enctype="multipart/form-data">
    File to import: <input type="file" name="file"/>
    <br/><br/>
    <input type="submit"/>
</form>

<br/>
<a href="countryList.form">Back</a>

<%@ include file="/WEB-INF/template/footer.jsp" %>
