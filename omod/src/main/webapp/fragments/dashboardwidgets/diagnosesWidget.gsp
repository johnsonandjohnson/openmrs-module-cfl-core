<div id="coreapps-diagnosesList" class="info-section">
    <div class="info-header">
        <i class="icon-diagnosis"></i>
        <h3>${ ui.message("coreapps.clinicianfacing.diagnoses").toUpperCase() }</h3>
    </div>
    <div class="info-body">
		<% if (!config.primaryDiagnosis && !config.secondaryDiagnosis) { %>
		    ${ ui.message("coreapps.none") }
		<% } else { %>
            <ul>
                <% config.primaryDiagnosis.each { %>
                    <li>
                        <% if(it.diagnosis.nonCodedAnswer) { %>
                            "${ui.escapeHtml(it.diagnosis.nonCodedAnswer)}"
                        <% } else { %>
                            ${ui.format(it.diagnosis.codedAnswer)}
                        <% } %>
                        <div class="tag">${ui.format(it.order)}</div>
                    </li>
                <% } %>
            </ul>
            <ul>
                <% config.secondaryDiagnosis.each { %>
                    <li>
                        <% if(it.diagnosis.nonCodedAnswer) { %>
                            "${ui.escapeHtml(it.diagnosis.nonCodedAnswer)}"
                        <% } else { %>
                            ${ui.format(it.diagnosis.codedAnswer)}
                        <% } %>
                    <div class="tag">${ui.format(it.order)}</div>
                    </li>
                <% } %>
            </ul>
		<% } %>
        <!-- <a class="view-more">${ ui.message("coreapps.clinicianfacing.showMoreInfo") } ></a> //-->
    </div>
</div>