<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("cfl.deletedPerson.breadcrumbLabel") }" }
    ];
</script>

<h1>${ ui.message("cfl.deletedPerson.title") }</h1>

<p>${ ui.message("cfl.deletedPerson.description") }</p>
