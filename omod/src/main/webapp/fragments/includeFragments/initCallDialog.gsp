<%
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
