<%
    // It is based on openmrs-module-coreapps/omod/src/main/webapp/pages/findpatient/findPatient.gsp.
    def areBreadcrumbsDefined = binding.hasVariable('breadcrumbs')

    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
<% if (areBreadcrumbsDefined) { %>
    var breadcrumbs = ${ breadcrumbs };
<% } else { %>
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message(label)}"}
    ];
<% } %>
    jq(function() {
        jq('#patient-search').focus();
    });
</script>

<h2>
	${ ui.message(heading) }
</h2>
<% if (areBreadcrumbsDefined) { %>
${ ui.includeFragment("cfl", "personSearch/personSearchWidget",
        [ afterSelectedUrl: afterSelectedUrl,
          showLastViewedPatients: showLastViewedPatients,
          breadcrumbOverride: breadcrumbs,
          registrationAppLink: registrationAppLink])}
<% } else { %>
${ ui.includeFragment("cfl", "personSearch/personSearchWidget",
        [ afterSelectedUrl: afterSelectedUrl,
          showLastViewedPatients: showLastViewedPatients,
          registrationAppLink: registrationAppLink ])}
<% } %>