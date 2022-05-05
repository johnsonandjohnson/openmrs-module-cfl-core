<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("cfldistribution.app.manageApps.title") ])

    ui.includeJavascript("cfldistribution", "manageApps.js");

    ui.includeCss("cfldistribution", "manageApps.css");

    ui.includeJavascript("appui", "jquery-3.4.1.min.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.systemAdministration.label")}",
          link: "${ui.pageLink("coreapps", "systemadministration/systemAdministration")}"
        },
        { label: "${ ui.message("cfldistribution.app.manageApps.title")}"}
    ];
</script>

<% apps.each { app -> %>
    ${ui.includeFragment("cfldistribution", "deleteUserApp", [appId: app.id])}
<% } %>

<h2>${ ui.message("cfldistribution.app.manageApps.heading")}</h2>

<button class="confirm" onclick="location.href='${ ui.pageLink("cfldistribution", "userApp", [action: "add"]) }'">
    ${ ui.message("cfldistribution.app.addAppDefinition") }
</button>
</br></br>

<table class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl">
    <thead>
    <tr>
        <th>${ ui.message("cfldistribution.app.appId.label")}</th>
        <th>${ ui.message("cfldistribution.app.status.label")}</th>
        <th>${ ui.message("cfldistribution.app.type.label")}</th>
        <th>${ ui.message("cfldistribution.app.actions.label")}</th>
    </tr>
    </thead>
    <% apps.each { app -> %>
    <tbody>
    <tr>
        <td>${app.id}</td>
        <td>
            <% if(app.enabled) { %>
            ${ui.message("cfldistribution.app.status.enabled")}
            <% } else { %>
            ${ui.message("cfldistribution.app.status.disabled")}
            <% } %>
        </td>
        <td>
            <% if(app.builtIn) { %>
            ${ui.message("cfldistribution.app.type.builtIn")}
            <% } else { %>
            ${ui.message("cfldistribution.app.type.implementationDefined")}
            <% } %>
        </td>
        <td>
            <form id="form-${app.id}" method="POST">
            <% if(!app.cannotBeStopped) { %>
                <% if(app.enabled) { %>
                    <i class="icon-stop stop-action cfldistribution-action"
                       title="${ ui.message("cfldistribution.app.action.disable") }"></i>
                    <input type="hidden" name="id" value="${app.id}"/>
                    <input type="hidden" name="action" value="disable" />
                <% } else { %>
                    <i class="icon-play play-action cfldistribution-action"
                       title="${ ui.message("cfldistribution.app.action.enable") }"></i>
                    <input type="hidden" name="id" value="${app.id}"/>
                    <input type="hidden" name="action" value="enable" />
                <% } %>
                <% if(!app.builtIn) { %>
                    <i class="icon-pencil edit-action" title="${ ui.message("general.edit") }"
                       onclick="location.href='${ui.pageLink("cfldistribution", "userApp", [appId: app.id, action: "edit"])}';"></i>
                    <i class="icon-remove delete-action" title="${ ui.message("general.delete") }"
                       onclick="showDeleteUserAppDialog('${app.id}')"></i>
                <% } %>
            <% } %>
            </form>
            <% if(app.cannotBeStopped) { %>
                <i class="icon-lock lock-action cfldistribution-action"></i>
            <% } %>
        </td>
    </tr>
    </tbody>
    <% } %>
</table>
