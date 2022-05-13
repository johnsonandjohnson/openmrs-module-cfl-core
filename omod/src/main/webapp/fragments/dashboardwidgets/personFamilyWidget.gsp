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
    ui.includeJavascript("coreapps", "web/coreapps.vendor.js")
    ui.includeJavascript("coreapps", "web/coreapps.dashboardwidgets.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")

    def editIcon = config.editIcon ?: "icon-share-alt"
    def personUuid = "{personUuid}"
    def recordIterator = 0
%>
<style>
.relationship-wrapper .tooltiptext {
    visibility: hidden;
    width: 200px;
    background-color: #555;
    color: #fff;
    text-align: center;
    border-radius: 6px;
    padding: 5px 0;
    position: absolute;
    z-index: 1;
    bottom: 125%;
    left: 50%;
    margin-left: -60px;
    opacity: 0;
    transition: opacity 0.3s;
}

.relationship-wrapper:hover .tooltiptext {
    visibility: visible;
    opacity: 1;
}

.relationship-wrapper {
    position: relative;
    display: inline-block;
}

.relationship-message {
    color: red;
    font-style: italic;
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
        <% if (relatedPeople.isEmpty()) { %>
            ${ui.message("coreapps.none")}
        <% } %>
       <ul>
            <% relatedPeople.each { person -> %>
                <% if(recordIterator < maxRecords) { %>
                    <li class="relationship-li">
                        <div class="relationship-wrapper">
                            <div class="fifty-percent relationship-inner">
                                <% def page = person.isPatient() ? config.patientPage : config.personPage %>
                                <% if(page && currentPersonLocation == person.location) { %>
                                <a href="/${contextPath}${page.replace(personUuid, person.uuid)}">
                                    <span>
                                        ${person.identifier == null ? "" : person.identifier}
                                        ${person.givenName}
                                        ${person.middleName == null ? "" : person.middleName}
                                        ${person.familyName}
                                        <g:set var="recordIterator" value="${recordIterator++}"/>
                                    </span>
                                </a>
                                <% } else { %>
                                    <span>
                                        ${person.identifier == null ? "" : person.identifier}
                                        ${person.givenName}
                                        ${person.middleName == null ? "" : person.middleName}
                                        ${person.familyName}
                                        <g:set var="recordIterator" value="${recordIterator++}"/>

                                <span class="tooltiptext">${ui.message("cfl.familyWidget.tooltip")}</span>
                                    </span>
                                <% } %>
                            </div>
                            <div class="tag forty-percent relationship-inner">
                                ${person.relationshipName}
                            </div>
                        </div>
                    </li>
              <% } %>
          <% } %>
          <% if(relatedPeople.size() > maxRecords) { %>
            <span class="relationship-message">
                ${String.format(ui.message("cfl.familyWidget.relationships.overflow.message"), maxRecords)}
            </span>
          <% } %>
       </ul>
    </div>
</div>
