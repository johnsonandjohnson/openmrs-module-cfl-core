<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("cfl.noPerson.breadcrumbLabel") }" }
    ];
</script>

<div  style="background-color:red;  border-radius: 5px">
<h1 style ="color:white">${ ui.message("cfl.personNotFound.description") }</h1>
</div>