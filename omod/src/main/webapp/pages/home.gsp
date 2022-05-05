<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("cfldistribution.home.title") ])
    ui.includeCss("cfldistribution", "home.css")

    def htmlSafeId = { extension ->
        "${ extension.id.replace(".", "-") }-${ extension.id.replace(".", "-") }-extension"
    }
%>

<div class="row">
    <div class="col-12 col-sm-12 col-md-12 col-lg-12 homeNotification">
        ${ ui.includeFragment("coreapps", "administrativenotification/notifications") }
    </div>
</div>
<div class="row">
    <div class="col-12 col-sm-12 col-md-12 col-lg-12">
        <% if (authenticatedUser) { %>
            <h4>
                ${ ui.encodeHtmlContent(ui.message("cfldistribution.home.currentUser", ui.format(authenticatedUser), ui.format(sessionContext.sessionLocation))) }
            </h4>
        <% } else { %>
            <h4>
                <a href="login.htm">${ ui.message("cfldistribution.home.logIn") }</a>
            </h4>
        <% } %>
    </div>
</div>
<div class="row">
    <div  class="col-12 col-sm-12 col-md-12 col-lg-12 homeList" id="apps">
            <% extensions.each { ext -> %>
                <a id="${ htmlSafeId(ext) }" href="/${ contextPath }/${ ext.url }" class="btn btn-default btn-lg button app big align-self-center" type="button">
                    <% if (ext.icon) { %>
                    <i class="${ ext.icon }"></i>
                    <% } %>
                    ${ ui.message(ext.label) }
                </a>
            <% } %>
    </div>
</div>
