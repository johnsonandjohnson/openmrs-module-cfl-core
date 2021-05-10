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

</style>

<div class="info-section">
    <div class="info-header" style="padding-bottom: 4px;">
        <i class="${config.icon}"></i>
        <h3>${ ui.message(config.label) }</h3>
    </div>
    <div class="info-body" style="padding-bottom: 4px;">
        <ul>
            <% programsList.each { program -> %>
                <li>
                    <% if(program.isEnrolled) { %>
                        <span class="cropped-text-span" title="${ui.format(program.name)}">${ui.format(program.name)}</span>
                        <span class="not-cropped-text-span"><a href="${ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi", ["patientId" : patientId,"encounterId":program.encounterId,"returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId="+patientId])}"><i class="icon-pencil edit-action" title="Edit"></i></a></span>
                        <span class="not-cropped-text-span"><i class="icon-remove delete-action" title="Discontinue" onclick="location.href = '${ ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", ["patientId" : patientId, "definitionUiResource": "cfl:htmlforms/" + program.programConfig.discontinuationFormName,"returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId="+patientId]) }'"></i></span>
                        <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.format(program.dateCompleted)}</div>
                    <% } else { %>
                        <% if(program.dateCompleted != null) { %>
                            <span class="not-cropped-text-span" title="${ui.format(program.name)}">${ui.format(program.name)}</span>
                            <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.format(program.dateCompleted)}</div>
                        <% } else { %>
                            <span class="not-cropped-text-span" title="${ui.format(program.name)}">${ui.format(program.name)}</span>
                            <span class="not-cropped-text-span"><button class="enrollButton" onClick = "location.href='${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", ["patientId" : patientId, "definitionUiResource": "cfl:htmlforms/" + program.programConfig.programFormName,"returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId="+patientId])}'">Enroll</button></span>
                        <% } %>
                    <% } %>
                </li>
            <% } %>
        </ul>
    </div>
</div>

