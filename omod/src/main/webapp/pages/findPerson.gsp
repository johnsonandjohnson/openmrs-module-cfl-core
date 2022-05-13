%{--
  - This Source Code Form is subject to the terms of the Mozilla Public License,
  - v. 2.0. If a copy of the MPL was not distributed with this file, You can
  - obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
  - the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
  - <p>
  - Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
  - graphic logo is a trademark of OpenMRS Inc.
  --}%

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
