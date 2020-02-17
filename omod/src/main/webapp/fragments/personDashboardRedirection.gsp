<% if(showInformationAboutActorDashboard) { %>
<div id="error-message" class="note-container">
    <div class="note warning" style="color: #5B57A6; border-color: #5B57A6; background-color: #F9F9F9;">
        <div class="text" style="display: flex; align-items: center; justify-content: center;">
            <i class="icon-info-sign medium"></i>
            <% if(isPatientDashboard) { %>
                <p style="position: inherit; margin-right: 0;">This Patient is also a ${ ui.message(actorTypeName)}.</p>
                &nbsp;
                <a href=${"?patientId=" + actorUuid + "&dashboard=person"}>
                See the ${ ui.message(actorTypeName)} Dashboard</a>
            <% } else { %>
                <p style="position: inherit; margin-right: 0;">This ${ ui.message(actorTypeName)} is also a Patient.</p>
                &nbsp;
                <a href=${"?patientId=" + actorUuid + "&dashboard=patient"}>See the Patient Dashboard</a>
            <% } %>
        </div>
    </div>
</div>
<% } %>
