<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    ui.includeJavascript("coreapps", "web/coreapps.vendor.js")
    ui.includeJavascript("coreapps", "web/coreapps.dashboardwidgets.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")

    def editIcon = config.editIcon ?: "icon-share-alt"
%>
<div id="coreapps-${config.id}" class="info-section openmrs-contrib-dashboardwidgets">
    <div class="info-header">
        <i class="${config.icon}"></i>
        <h3>${ ui.message(config.label) }</h3>
        <% if (editIcon && config.detailsUrl) { %>
            <i class="${editIcon} edit-action right" title="${ ui.message("coreapps.edit") }"
               onclick="location.href='${ ui.urlBind("/" + contextPath + "/" + config.detailsUrl, [ "patient.uuid": config.patientUuid ]) }';"></i>
        <% } %>
    </div>
    <div class="info-body">
        <${config.widget} config="${config.json}"></${config.widget}>
    </div>
</div>
