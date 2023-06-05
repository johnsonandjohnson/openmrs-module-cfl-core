<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<% if(showInformationAboutActorDashboard) { %>
<div id="error-message" class="note-container">
    <div class="note warning" style="color: #5B57A6; border-color: #5B57A6; background-color: #F9F9F9;">
        <div class="text" style="display: flex; align-items: center; justify-content: center;">
            <i class="icon-info-sign medium"></i>
            <% if(isPatientDashboard) { %>
                <p style="position: inherit; margin-right: 0;">${ui.message("cfl.personDashboardRedirection.label1", actorTypeName)}</p>
                &nbsp;
                <a href=${"?patientId=" + actorUuid + "&dashboard=person"}>
                ${ui.message("cfl.personDashboardRedirection.label2", actorTypeName)}</a>
            <% } else { %>
                <p style="position: inherit; margin-right: 0;">${ui.message("cfl.personDashboardRedirection.label3", actorTypeName)}</p>
                &nbsp;
                <a href=${"?patientId=" + actorUuid + "&dashboard=patient"}>${ui.message("cfl.personDashboardRedirection.label4")}</a>
            <% } %>
        </div>
    </div>
</div>
<% } %>
