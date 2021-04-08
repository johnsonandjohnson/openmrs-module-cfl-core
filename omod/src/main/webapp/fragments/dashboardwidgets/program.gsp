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

    .deactivateButtonDiv {
        margin-left: 95px;
        margin-top: 10px;
        margin-bottom: 10px;
    }

    .deactivateButton {
        padding: 5px;
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
                    ${ui.format(program.name)}
                    <% if(program.isEnrolled) { %>
                        <a href="${ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi", ["patientId" : patientId,"encounterId":encounterId,"returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId="+patientId])}">Edit</a>
                        <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.format(program.dateCompleted)}</div>
                        <div class="deactivateButtonDiv">
                            <button class="deactivateButton" onclick="location.href = '${ ui.pageLink("cfl", "deactivateProgram", [patientId: patientId,"patientProgramId":patientProgramId,"returnUrl":"/coreapps/clinicianfacing/patient.page?patientId="+patientId]) }'">Deactivate</button>
                        </div>
                    <% } else { %>
                        <% if(program.isVoided) { %>
                            <div class="tag sixty-percent">${ui.format(program.dateEnrolled)} - ${ui.format(program.dateCompleted)}</div>
                        <% } else { %>
                            <a href="${ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", ["patientId" : patientId, "definitionUiResource": "cfl:htmlforms/"+config.formName,"returnUrl":"/openmrs/coreapps/clinicianfacing/patient.page?patientId="+patientId])}">Enroll</a>
                        <% } %>
                    <% } %>
                </li>
            <% } %>
        </ul>
    </div>
</div>

