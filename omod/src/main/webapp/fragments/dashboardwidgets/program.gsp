<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<style>
.disabled-time {
    border: 3px solid red;
}

.time-entry-wrapper .tooltiptext {
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
    left: 20%;
    margin-left: -60px;
    opacity: 0;
    transition: opacity 0.3s;
}

.time-entry-wrapper:hover .tooltiptext {
    visibility: visible;
    opacity: 1;
}

.time-entry-wrapper {
    position: relative;
    display: inline-block;
}

.cropped-text-span {
    display: block;
    white-space: nowrap;
    width: 45px;
    overflow: hidden;
    text-overflow: ellipsis;
    font-weight: bold;
}

.not-cropped-text-span {
    display: block;
    white-space: nowrap;
    overflow: hidden;
    font-weight: bold;
}

.enrollButton {
    background-color: #CCCCCC;
    border: none;
    color: black;
    padding: 5px;
    font-size: 15px;
    border-radius: 4px;
    margin: right;
}

.single-entry {
    display: flex;
    align-items: center;
}

.flex-class {
    flex: 1;
}

.program-name {
    margin-right: 5px;
}

</style>

<div class="info-section">
    <div class="info-header" style="padding-bottom: 4px;">
        <i class="${config.icon}"></i>
        <h3>${ui.message(config.label)}</h3>
    </div>
    <div class="info-body" style="padding-bottom: 4px;">
        <ul>
            <% patientProgramsDetailsList.each { program -> %>
            <li class="single-entry">
                <% if(program.isEnrolled) { %>
                    <span class="cropped-text-span flex-class program-name" title="${ui.format(program.programName)}">${ui.format(program.programName)}</span>
                    <% if(program.programConfig.enterModeOnly) { %>
                        <span class="not-cropped-text-span">
                            <a href="${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi",
                            ["patientId" : program.patient.patientId, "formUuid" : program.programConfig.programFormUuid,
                            "returnUrl" : "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + program.patient.patientId])}">
                            <i class="icon-pencil edit-action" title=${ui.message("common.edit")}></i>
                            </a>
                        </span>
                    <% } else { %>
                        <span class="not-cropped-text-span">
                            <a href="${ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi",
                            ["patientId" : program.patient.patientId, "encounterId":program.encounterId,
                            "returnUrl" : "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + program.patient.patientId])}">
                            <i class="icon-pencil edit-action" title=${ui.message("common.edit")}></i>
                            </a>
                        </span>
                    <% } %>
                    <span class="not-cropped-text-span">
                        <i class="icon-remove delete-action" title=${ui.message("cfl.discontinue.label")}
                           onclick="location.href = '${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi",
                           ["patientId" : program.patient.patientId, "formUuid": program.programConfig.discontinuationFormUuid,
                           "returnUrl" : "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + program.patient.patientId])}'">
                        </i>
                    </span>
                    <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.message("cfl.programsWidget.current.label")}</div>
                <% } else { %>
                    <% if(program.dateCompleted != null) { %>
                        <span class="not-cropped-text-span flex-class program-name" title="${ui.format(program.programName)}">${ui.format(program.programName)}</span>
                        <span class="not-cropped-text-span">
                            <a href="${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi",
                                ["patientId" : program.patient.patientId, "formUuid" : program.programConfig.programFormUuid,
                                "returnUrl" : "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + program.patient.patientId])}">
                                <i class="icon-pencil edit-action" title=${ui.message("common.edit")}></i>
                            </a>
                        </span>
                        <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.format(program.dateCompleted)}</div>
                    <% } else { %>
                        <span class="not-cropped-text-span program-name" title="${ui.format(program.programName)}">${ui.format(program.programName)}</span>
                        <span class="not-cropped-text-span">
                            <button class="enrollButton"
                                onClick = "location.href='${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi",
                                ["patientId" : program.patient.patientId, "formUuid" : program.programConfig.programFormUuid,
                                "returnUrl" : "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + program.patient.patientId])}'">${ui.message("cfl.programsWidget.enrolButton.label")}
                            </button>
                        </span>
                    <% } %>
                <% } %>
            </li>
            <% } %>
        </ul>
    </div>
</div>

