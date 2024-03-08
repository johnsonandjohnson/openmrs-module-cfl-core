<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    ui.includeCss("cfl", "disabledInitCall.css")
    ui.includeJavascript("cfl", "initCall.js")
%>

<div id="init-call-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>
            ${ ui.message("cfl.initCall.title") }
        </h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("cfl.initCall.message") }</p>
        <button class="confirm right">${ ui.message("coreapps.confirm") }
            <i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
        </button>
        <button class="cancel">${ ui.message("coreapps.cancel") }</button>
    </div>
</div>

<div id="dialog-background"></div>
<div id="disabled-init-call-dialog" class="dialog">
    <div class="dialog-header">
        <div class="dialog-header-img">
            <svg xmlns="http://www.w3.org/2000/svg" width="70" height="70" fill="#1a445a" class="bi bi-telephone" viewBox="0 0 16 16">
                <path d="M3.654 1.328a.678.678 0 0 0-1.015-.063L1.605 2.3c-.483.484-.661 1.169-.45 1.77a17.568 17.568 0 0 0 4.168 6.608 17.569 17.569 0 0 0 6.608 4.168c.601.211 1.286.033 1.77-.45l1.034-1.034a.678.678 0 0 0-.063-1.015l-2.307-1.794a.678.678 0 0 0-.58-.122l-2.19.547a1.745 1.745 0 0 1-1.657-.459L5.482 8.062a1.745 1.745 0 0 1-.46-1.657l.548-2.19a.678.678 0 0 0-.122-.58L3.654 1.328zM1.884.511a1.745 1.745 0 0 1 2.612.163L6.29 2.98c.329.423.445.974.315 1.494l-.547 2.19a.678.678 0 0 0 .178.643l2.457 2.457a.678.678 0 0 0 .644.178l2.189-.547a1.745 1.745 0 0 1 1.494.315l2.306 1.794c.829.645.905 1.87.163 2.611l-1.034 1.034c-.74.74-1.846 1.065-2.877.702a18.634 18.634 0 0 1-7.01-4.42 18.634 18.634 0 0 1-4.42-7.009c-.362-1.03-.037-2.137.703-2.877L1.885.511z"/>
            </svg>
        </div>
        <h3>${ ui.message("cfl.initCall.disabled.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">
            <span>${ ui.message("cfl.initCall.disabled.message.part1") }</span>
            <br/>
            <span>${ ui.message("cfl.initCall.disabled.message.part2") }</span>
        </p>
        <audio
            id="player"
            controls
            controlslist="nodownload"
            src="${ui.resourceLink("cfl", "audio/Cfl_Recording.mp3")}">
        </audio>
        <button class="confirm">${ ui.message("cfl.initCall.disabled.button") }</button>
    </div>
</div>