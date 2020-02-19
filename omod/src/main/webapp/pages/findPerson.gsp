<%
    // It is based on openmrs-module-coreapps/omod/src/main/webapp/pages/findpatient/findPatient.gsp.
    def breadcrumbMiddle = breadcrumbOverride ?: ''

    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    jq(function() {
        jq('#patient-search').focus();
    });

    var breadcrumbs = _.compact(_.flatten([
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        ${ breadcrumbMiddle },
        {
            label: "${ ui.message(label) }",
            link: "${ ui.pageLink("cfl", "findPerson") }"
        }
    ]));
</script>

<h2>
	${ ui.message(heading) }
</h2>
<% if (breadcrumbOverride) { %>
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
