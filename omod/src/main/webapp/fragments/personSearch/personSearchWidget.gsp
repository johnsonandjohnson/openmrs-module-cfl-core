<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    // It is based on openmrs-module-coreapps/omod/src/main/webapp/fragments/patientsearch/patientSearchWidget.gsp.
    def breadcrumbOverride = config.breadcrumbOverride ?: ""

    ui.includeCss("uicommons", "datatables/dataTables_jui.css")
    ui.includeCss("coreapps", "patientsearch/patientSearchWidget.css")
    ui.includeCss("cflcore", "personSearch/personSearchWidget.css")
    ui.includeJavascript("cflcore", "personSearch/personSearchWidget.js")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")
    ui.includeJavascript("uicommons", "moment-with-locales.min.js")
%>
<script type="text/javascript">
    var listableAttributeTypes = [];
    <% listingAttributeTypeNames.each { %>
        listableAttributeTypes.push('${ ui.encodeHtml(it) }');
    <% } %>
    var lastViewedPatients = [];
    <%  if (showLastViewedPatients && !doInitialSearch) {
            lastViewedPatients.each { it -> %>

    var patientObj = {
        uuid:"${ ui.escapeJs(ui.encodeHtmlContent(it.uuid)) }",
        name:"${ it.personName ? ui.escapeJs(ui.encodeHtmlContent(ui.format(it.personName))) : '' }",
        gender:"${ ui.escapeJs(ui.encodeHtmlContent(it.gender)) }",
        // it.age is of type int (doesn't need sanitization)
        age:"${ it.age ?: '' }",
        birthdate:"${ it.birthdate ? ui.escapeJs(ui.encodeHtmlContent(it.birthdate)) : '' }",
        // it.birthdateEstimated is of type boolean (doesn't need sanitization)
        birthdateEstimated: ${ it.birthdateEstimated },
        personIdentifier:"${ it.personIdentifier ? ui.escapeJs(ui.encodeHtmlContent(it.personIdentifier)) : '' }",
        patientIdentifier:"${ it.patientIdentifier ? ui.escapeJs(ui.encodeHtmlContent(it.patientIdentifier)) : '' }",
        widgetBirthdate:"${ it.birthdate ? ui.escapeJs(ui.encodeHtmlContent(it.birthdate)) : '' }"
    }
        <% listingAttributeTypeNames.each { attributeName -> %>
            patientObj["${ ui.encodeHtml(attributeName) }"] = "${ it.getAttribute(attributeName) ?
                ui.encodeHtml(it.getAttribute(attributeName).value) : '' }";
        <% } %>
    lastViewedPatients.push(patientObj);
    <%      }
        }%>
    function handlePatientRowSelection() {
    	var afterSelectedUrl = '${ ui.escapeJs(config.afterSelectedUrl) }';
        this.handle = function (row) {
            var uuid = row.uuid;
            if(afterSelectedUrl && afterSelectedUrl != 'null') {
            	location.href = '/' + OPENMRS_CONTEXT_PATH + emr.applyContextModel(afterSelectedUrl, { patientId: uuid, breadcrumbOverride: '${ ui.encodeForSafeURL(breadcrumbOverride) }'});
        	} else {
        		jQuery("#patient-search").attr("selected_uuid", uuid);
        		jQuery("#patient-search").attr("selected_name", row.person.personName.display);
        		jQuery("#patient-search").val("");
        		jQuery("#patient-search-results-table tbody").html("");
        		jQuery("#patient-search").change();
        	}
        }
    }
    var handlePatientRowSelection =  new handlePatientRowSelection();

    var patientSearchWidget;
    jq(function() {
        var widgetConfig = {
            initialPatients: lastViewedPatients,
            doInitialSearch: ${ doInitialSearch ? "\"" + ui.escapeJs(doInitialSearch) + "\"" : "null" },
            minSearchCharacters: ${ minSearchCharacters ?: 3 },
            afterSelectedUrl: '${ ui.escapeJs(config.afterSelectedUrl) }',
            breadcrumbOverride: '${ ui.escapeJs(breadcrumbOverride) }',
            searchDelayShort: ${ searchDelayShort },
            searchDelayLong: ${ searchDelayLong },
            handleRowSelection: ${ config.rowSelectionHandler ?: "handlePatientRowSelection" },
            dateFormat: '${ dateFormatJS }',
            locale: '${ locale }',
            defaultLocale: '${ defaultLocale }',
            attributeTypes: listableAttributeTypes,
            messages: {
                info: '${ ui.message("coreapps.search.info") }',
                first: '${ ui.message("coreapps.search.first") }',
                previous: '${ ui.message("coreapps.search.previous") }',
                next: '${ ui.message("coreapps.search.next") }',
                last: '${ ui.message("coreapps.search.last") }',
                noMatchesFound: '${ ui.message("coreapps.search.noMatchesFound") }',
                noData: '${ ui.message("coreapps.search.noData") }',
                recent: '${ ui.message("coreapps.search.label.recent") }',
                onlyInMpi: '${ ui.message("coreapps.search.label.onlyInMpi") }',
                searchError: '${ ui.message("coreapps.search.error") }',
                identifierColHeader: '${ ui.message("coreapps.search.identifier") }',
                nameColHeader: '${ ui.message("coreapps.search.name") }',
                genderColHeader: '${ ui.message("coreapps.gender") }',
                ageColHeader: '${ ui.message("coreapps.age") }',
                birthdateColHeader: '${ ui.message("coreapps.birthdate") }',
                ageInMonths: '${ ui.message("coreapps.age.months") }',
                ageInDays: '${ ui.message("coreapps.age.days") }'
            }
        };

        patientSearchWidget = new PersonSearchWidget(widgetConfig);
    });
</script>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <form method="get" id="patient-search-form" onsubmit="return false">
            <input class="form-control input-sm input-lg" type="text" id="patient-search" placeholder="${ ui.message("cfl.findPerson.search.placeholder") }" autocomplete="off" <% if (doInitialSearch) { %>value="${doInitialSearch}"<% } %>/><i id="patient-search-clear-button" class="small icon-remove-sign"></i>
            <% if(patientSearchExtensions){

                patientSearchExtensions.each {
                    // create a base map from the fragmentConfig if it exists, otherwise just create an empty map
                    def configs = [:];
                    if(it.extensionParams.fragmentConfig != null){
                        configs = it.extensionParams.fragmentConfig;
                    }
                %>
                    ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, configs) }
                <%}
            } %>
        </form>
    </div>
</div>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div id="patient-search-results"></div>
    </div>
</div>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <%  if (registrationAppLink ?: false) { %>
        <div id="register-patient-link">
            <label>${ ui.message("coreapps.findPatient.registerPatient.label") }&nbsp;&nbsp;</label><a id="patient-search-register-patient" class="button" href="/${contextPath}/${registrationAppLink}">${ui.message("registrationapp.registration.label")}</a>
        </div>
        <%  } %>
    </div>
</div>
