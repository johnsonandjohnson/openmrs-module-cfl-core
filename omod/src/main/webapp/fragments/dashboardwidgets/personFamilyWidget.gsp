<%
    ui.includeJavascript("coreapps", "web/coreapps.vendor.js")
    ui.includeJavascript("coreapps", "web/coreapps.dashboardwidgets.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")

    def editIcon = config.editIcon ?: "icon-share-alt"
    def relatedPeopleIterator = 0
    def relationshipNamesIterator = 0
    def personUuid = "{personUuid}"
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
        <% if (relatedPeople.isEmpty()) { %>
            ${ui.message("coreapps.none")}
        <% } %>
       <ul>
            <% relatedPeople.each { person -> %>
                <li class="relationship-li">
                    <div class="relationship-wrapper">
                        <div class="fifty-percent relationship-inner">                       
                            <% def page = person.isPatient() ? config.patientPage : config.personPage %> 
                            <a href="/${contextPath}${page.replace(personUuid, person.uuid)}">
                                <% if (!relatedPeopleIdentifiers.isEmpty()) { %>
                                    ${relatedPeopleIdentifiers.get(relatedPeopleIterator++)}
                                <% } %>
                                ${person.givenName} 
                                ${person.middleName == null ? "" : person.middleName} 
                                ${person.familyName}
                            </a> 
                        </div>
                        <div class="tag forty-percent relationship-inner">
                            ${relationshipNames.get(relationshipNamesIterator++)}
                        </div>
                    </div>
                </li>
            <% } %>
       </ul>
    </div>
</div>
