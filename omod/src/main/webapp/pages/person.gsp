<%
    def isPatientDashboard = dashboard == "patientDashboard"
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("coreapps", "clinicianfacing/patient.css")
    ui.includeJavascript("coreapps", "custom/deletePatient.js")
    ui.includeJavascript("cfl", "deletePerson.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(isPatientDashboard ? patient.patient : person.person))) }" ,
            link: '${ ui.urlBind("/" + contextPath + baseDashboardUrl, [ patientId: isPatientDashboard ? patient.patient.id : person.person.id ] ) }'}
    ];
    // add on breadcrumb if it has been defined in messages.properties
    <% if (ui.message(dashboard + ".breadcrumb") != dashboard + ".breadcrumb") { %>
        breadcrumbs.push({ label: "${ ui.message(dashboard + ".breadcrumb") }"})
    <% } %>
    jq(function(){
        jq(".tabs").tabs();
        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function() {
            window.location.reload();
        });
    });
    <% if (isPatientDashboard) { %>
        var patient = { id: ${patient.id} };
    <% } %>
</script>

<% if(includeFragments){
    includeFragments.each {
        // create a base map from the fragmentConfig if it exists, otherwise just create an empty map
        def configs = [:];
        if(it.extensionParams.fragmentConfig != null){
            configs = it.extensionParams.fragmentConfig;
        }
        configs.patient = patient;   // add the patient to the map %>
        ${ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, configs)}
    <%}
} %>

<% if(isPatientDashboard) { %>
    ${ ui.includeFragment("cfl", "patientHeader", [ patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel ]) }
<% } else { %>
    ${ ui.includeFragment("cfl", "personHeader", [ person: person.person, appContextModel: appContextModel ]) }
<% } %>

${ ui.includeFragment("cfl", "personDashboardRedirection",
        [ isPatientDashboard: isPatientDashboard, actorUuid: isPatientDashboard ? patient.patient.uuid : person.person.uuid ]) }

<div class="clear"></div>
<div class="container">
    <div class="dashboard clear">
        <!-- only show the title div if a title has been defined in the messages.properties -->
        <% if (ui.message(dashboard + ".custom.title") != dashboard + ".custom.title") { %>
        <div class="title">
            <h3>${ ui.message(dashboard + ".custom.title") }</h3>
        </div>
        <% } %>
        <div class="info-container column">
            <% if (firstColumnFragments) {
                firstColumnFragments.each {
                    // create a base map from the fragmentConfig if it exists, otherwise just create an empty map
                    def configs = [:];
                    if(it.extensionParams.fragmentConfig != null){
                        configs = it.extensionParams.fragmentConfig;
                    }
                    configs << [ patient: patient, patientId: isPatientDashboard ? patient.patient.id : person.person.id, app: it.appId ]
            %>
                    ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, configs)}
            <% }
            } %>

        </div>
        <div class="info-container column">
            <% if (secondColumnFragments) {
                secondColumnFragments.each {
                    // create a base map from the fragmentConfig if it exists, otherwise just create an empty map
                    def configs = [:];
                    if(it.extensionParams.fragmentConfig != null){
                        configs = it.extensionParams.fragmentConfig;
                    }
                    configs << [ patient: patient, patientId: isPatientDashboard ? patient.patient.id : person.person.id, app: it.appId ]
            %>
                    ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, configs)}
            <% }
            } %>

        </div>
        <% if ((visitActions && visitActions.size() > 0) || (overallActions && overallActions.size() > 0) || (otherActions && otherActions.size() > 0))  { %>
            <div class="action-container column">
                <div class="action-section">
                    <% if (activeVisit && visitActions && visitActions.size() > 0) { %>
                        <ul class="float-left">
                            <h3 >${ ui.message("coreapps.clinicianfacing.activeVisitActions") }</h3>
                            <% visitActions.each { ext -> %>
                            <li class="float-left">
                                <a href="${ ui.escapeJs(ext.url("/" + ui.contextPath(), appContextModel, ui.thisUrl())) }" id="${ ext.id }" class="float-left">
                                    <i class="${ ext.icon } float-left"></i>
                                    ${ ui.message(ext.label) }
                                </a>
                            </li>
                            <% } %>
                        </ul>
                    <% } %>
                    <% if (overallActions && overallActions.size() > 0) { %>
                        <ul class="float-left">
                            <h3>${ ui.message("coreapps.clinicianfacing.overallActions") }</h3>
                            <%
                                overallActions.each { ext -> %>
                                    <li class="float-left">
                                        <a href="${ ui.escapeJs(ext.url("/" + ui.contextPath(), appContextModel, ui.thisUrl())) }" id="${ ext.id }" class="float-left">
                                            <i class="${ ext.icon } float-left"></i>
                                            ${ ui.message(ext.label) }
                                        </a>
                                    </li>
                            <% } %>
                        </ul>
                    <% } %>
                    <%
                     def cxtModel = [ patientId: isPatientDashboard ? patient.patient.id : person.person.id, activeVisitId: activeVisit ? activeVisit.visit.id : null]
                     otherActions.each { action -> %>
                    <a id="${ action.id }" class="button medium" href="${ ui.escapeJs(action.url("/" + ui.contextPath(), cxtModel)) }" class="float-left">
                        <i class="${ action.icon } float-left"></i>${ ui.message(action.label) }
                    </a>
                    <% } %>
                </div>
            </div>
        <% } %>
    </div>
</div>
<% if (!isPatientDashboard) { %>
    <div id="delete-person-creation-dialog" class="dialog" style="display: none">
        <div class="dialog-header">
            <i class="icon-remove"></i>
            <h3>
                ${ ui.message("cfl.deletePerson.title", ui.encodeHtmlContent(ui.format(person.person))) }
            </h3>
        </div>
        <div class="dialog-content">
            <p class="dialog-instructions">${ ui.message("cfl.deletePerson.message", ui.encodeHtmlContent(ui.format(person.person))) }</p>
            <label for="delete-reason">${ ui.message("cfl.deletePerson.reason.label") }: </label>
            <input type="text" id="delete-reason">
            <br>
            <h6 id="delete-reason-empty">${ ui.message("cfl.deletePerson.deleteMessageEmpty") }</h6>
            <br>
            <button class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
            <button class="cancel">${ ui.message("coreapps.cancel") }</button>
        </div>
    </div>
<% } %>
