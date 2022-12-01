<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    def person = config.person.person
    def personNames = config.personNames
    def telephone = config.telephone
    ui.includeCss("coreapps", "patientHeader.css")
    ui.includeJavascript("coreapps", "patientdashboard/patient.js")
    appContextModel.put("returnUrl", ui.thisUrl())
%>

<script type="text/javascript">
    jq(document).ready(function () {
        jq(".demographics .name").click(function () {
            emr.navigateTo({
                url: "${ ui.urlBind("/" + contextPath + config.dashboardUrl, [ patientId: person.uuid ] ) }"
            });
        })
    })
</script>

<div class="patient-header <% if (person.dead) { %>dead<% } %>">

    <% if (person.dead) { %>
    <div class="death-header">
        <span class="death-message">
            ${ ui.message("coreapps.deadPatient", ui.format(person.deathDate), ui.format(person.causeOfDeath)) }
        </span>
        <span class="death-info-extension">
            ${ ui.includeFragment("appui", "extensionPoint", [ id: "patientHeader.deathInfo", contextModel: appContextModel ]) }
            <% if (context.hasPrivilege("Task: coreapps.markPatientDead")) { %>
            <a href="${ ui.pageLink("coreapps", "markPatientDead",[patientId: person.id]) }"><i class="icon-pencil edit-action" title="${ ui.message("coreapps.edit") }"></i></a>
            <% } %>
        </span>
    </div>
    <% } %>

    <div class="demographics">
        <h1 class="name">
            <div style="display: flex; align-items: center;">
                <% personNames?.each { %>
                <span><span class="${ it.key.replace('.', '-') }">${ ui.encodeHtmlContent(it.value) }</span><em>${ui.message(it.key)}</em></span>&nbsp;
                <% } %>
                &nbsp;
                <div style="display: flex; flex-direction: column;">
                    <% if (isShowGenderPersonHeader) { %>
                        <% if (person.gender) { %>
                            <span>${ui.message("coreapps.gender." + ui.encodeHtml(person.gender))}&nbsp;</span>
                        <% } else { %>
                            <span>${ui.message("cfl.personHeader.genderSetFalse")}</span>
                        <% } %>
                    <% } else { %>
                        <span>${ui.message("cfl.personHeader.genderSetFalse")}</span>
                    <% } %>
                    <% if (isShowAgePersonHeader) { %>
                        <span>
                            <% if (person.birthdate) { %>
                                <% if (person.age > 0) { %>
                                    ${ui.message("coreapps.ageYears", person.age)}
                                <% } else if (person.ageInMonths > 0) { %>
                                    ${ui.message("coreapps.ageMonths", person.ageInMonths)}
                                <% } else { %>
                                    ${ui.message("coreapps.ageDays", person.ageInDays)}
                                <% } %>
                                (<% if (person.birthdateEstimated) { %>~<% } %>
                                ${ ui.formatDatePretty(person.birthdate) })
                            <% } else { %>
                                ${ui.message("coreapps.unknownAge")}
                            <% } %>
                        </span>
                    <% } else { %>
                        <span>${ui.message("coreapps.unknownAge")}</span>
                    <% } %>
                    <% if (telephone) { %>
                        <span class="gender-age">
                            <span>${ui.message("cfl.telephone")}:&nbsp;</span>
                            <span>${telephone}</span>
                        </span>
                    <% } %>
                </div>
            </div>

            <div class="firstLineFragments">
                <% firstLineFragments.each { %>
                ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, [patient: config.person])}
                <% } %>
            </div>
        </h1>
    </div>

    <% if (personIdentifier) { %>
        <div class="identifiers">
            <em>${ ui.message(personIdentifierLabel) }</em>
            <span>${ personIdentifier }</span>
        </div>
    <% } %>

    <div class="secondLineFragments">
        <% secondLineFragments.each { %>
        ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, [patient: config.person])}
        <% } %>
    </div>

    <div class="close"></div>
</div>
