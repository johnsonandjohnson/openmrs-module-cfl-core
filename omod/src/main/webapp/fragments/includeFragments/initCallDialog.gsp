<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<style>
  #initCallConfirmButton {
    background-image: none;
    background-color: #00455c;
    border-color: #00455c;
    margin: 0;
    font-size: 11px;
    text-transform: uppercase;
  }

  #initCallCancelButton {
    background: transparent;
    border: #000000 1px solid !important;
    color: #00455c;
    font-size: 11px;
    text-transform: uppercase;
  }
</style>

<%
    ui.includeJavascript("cflcore", "initCall.js")
%>

<div id="init-call-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>
            ${ ui.message("cfl.initCall.title") }
        </h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("cfl.initCall.message") }</p>
        <button id="initCallConfirmButton" class="confirm right">${ ui.message("coreapps.confirm") }
            <i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
        </button>
        <button id="initCallCancelButton" class="cancel">${ ui.message("coreapps.cancel") }</button>
    </div>
</div>
