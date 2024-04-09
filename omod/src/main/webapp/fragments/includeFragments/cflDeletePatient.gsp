<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<style>
  .dialog-instructions {
    padding-top: 0 !important;
  }

  #deletePatientCancelButton {
    background: transparent;
    border: #000000 1px solid !important;
    color: #00455c;
    margin: 0;
    font-size: 11px;
    text-transform: uppercase;
  }

  #deletePatientConfirmButton {
    background-image: none;
    background-color: #00455c;
    border-color: #00455c;
    margin: 0;
    font-size: 11px;
    text-transform: uppercase;
  }
</style>

<%
  ui.includeJavascript("cflcore", "cflDeletePatient.js")
%>

<div id="cfl-delete-patient-creation-dialog" class="dialog" style="display: none">
  <div class="dialog-header">
    <i class="icon-remove"></i>
    <h3>
        <span>${ ui.message("cfl.deletePatientButton.label") }</span>:
        <span>${ ui.encodeHtmlContent(ui.format(patient.patient)) }</span>
    </h3>
  </div>
  <div class="dialog-content">
    <p class="dialog-instructions">${ ui.message("coreapps.task.deletePatient.message", ui.encodeHtmlContent(ui.format(patient.patient))) }?</p>

    <label for="cfl-delete-reason">${ ui.message("coreapps.retrospectiveCheckin.paymentReason.label") }: </label>
    <input type="text" id="cfl-delete-reason">

    <br>
    <span id="cfl-delete-reason-empty">${ ui.message("coreapps.task.deletePatient.deleteMessageEmpty") }</span>
    <br>
    <button id="deletePatientConfirmButton" class="confirm right">${ ui.message("coreapps.confirm") }
      <i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
    </button>
    <button id="deletePatientCancelButton" class="cancel">${ ui.message("coreapps.cancel") }</button>
  </div>
</div>