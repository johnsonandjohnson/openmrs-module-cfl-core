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
    def recordIterator = 0
    def textListsIterator = 0
    def encounterStatusIterator = 0
%>

<style>
    .dashboard .info-body li {
       line-height: 1.2em;
    }

    .dashboard .encounter-link {
       color: #363463;
       text-decoration: underline;
       cursor: pointer;
    }
</style>

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
        <% if (encounters.isEmpty()) { %>
            ${ui.message("coreapps.none")}
        <% } %>

        <ul>
            <% encounters.each { encounter -> %>
            <% if(recordIterator < maxRecords && (!encountersBoolList.get(encounterStatusIterator) && textsList.get(textListsIterator) != "")) { %>
                <li class="clear">
                    <a href="${ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi", ["patientId" : patientId, "encounterId" : encounter.encounterId, "returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + patientId])}" class="encounter-link">
                        <span>${encounter.dateCreated} ${textsList.get(textListsIterator)}</span>
                    </a>
                    <div class="tag sixty-percent">
                        ${(textsList.get(textListsIterator)).toUpperCase()}
                    </div>
                    <script type="text/javascript">
                        jq('.encounter-link[href="${ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi", ["patientId" : patientId, "encounterId" : encounter.encounterId, "returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + patientId])}"]')
                            .text(moment.utc("${ encounter.dateCreated }").local().format('DD MMM YYYY'));
                    </script>
                </li>
                <% } %>
                <g:set var="recordIterator" value="${recordIterator++}"/>
                <g:set var="textListsIterator" value="${textListsIterator++}"/>
                <g:set var="encounterStatusIterator" value="${encounterStatusIterator++}"/>
            <% } %>
        </ul>
    </div>
</div>

