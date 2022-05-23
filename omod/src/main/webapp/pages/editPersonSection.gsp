<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js", Integer.MAX_VALUE - 1);
    ui.includeJavascript("uicommons", "navigator/validators.js", Integer.MAX_VALUE - 19)
    ui.includeJavascript("uicommons", "navigator/navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("uicommons", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("uicommons", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("uicommons", "navigator/navigatorTemplates.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("uicommons", "navigator/exitHandlers.js", Integer.MAX_VALUE - 22);

    ui.includeCss("registrationapp", "editSection.css")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    Calendar cal = Calendar.getInstance()
    def maxAgeYear = cal.get(Calendar.YEAR)
    def minAgeYear = maxAgeYear - 120

    if (!returnUrl) {
        returnUrl = "/${contextPath}/coreapps/patientdashboard/patientDashboard.page?patientId=${person.personId}"
    }
%>

${ ui.includeFragment("uicommons", "validationMessages")}

<script type="text/javascript">
    var NavigatorController;
    jQuery(function() {

        NavigatorController = KeyboardController();
        jq('#cancelSubmission').unbind(); // unbind the functionality built into the navigator to return to top of the form
        jq('#cancelSubmission').click(function(event){
            window.location='${ ui.encodeJavaScript(returnUrl) }';
        })

        // disable submit button on submit
        jq('#registration-section-form').submit(function() {
            jq('#registration-submit').attr('disabled', 'disabled');
            jq('#registration-submit').addClass("disabled");
        })

        // clicking the save form link should have the same functionality as clicking on the confirmation section title (ie, jumps to confirmation)
        jq('#save-form').click(function() {
            NavigatorController.getSectionById("confirmation").title.click();
        })

    });
</script>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(person)) }", link: "${ ui.encodeHtml(returnUrl) }" },
        { label: "${ ui.message(section.label) }" }
    ];
</script>

<h2>
    ${ ui.message(section.label)  }
</h2>

<div id="exit-form-container">
    <a id="save-form">
        <i class="icon-save small"></i>
        ${ ui.message("htmlformentryui.saveForm") }
    </a>
    <% if (returnUrl) { %>
        <a href="${ ui.escapeAttribute(returnUrl) }">
            <i class="icon-signout small"></i>
            ${ ui.message("htmlformentryui.exitForm") }
        </a>
    <% } %>
</div>

<form
        id="registration-section-form"
        class="simple-form-ui ${ section.skipConfirmation ? 'skip-confirmation-section' : '' }"
        method="POST"
        action="/${ contextPath }/cfl/editPersonSection.page?personId=${ person.personId }&returnUrl=${ ui.urlEncode(returnUrl) }&appId=${ app.id }&sectionId=${ ui.encodeHtml(section.id) }">

    <section id="${ section.id }" class="non-collapsible">

        <span id="${ section.id }_label" class="title">
            ${ ui.message(section.label) }
        </span>
        <% section.questions.each { question ->
                def fields=question.fields
        %>
            <fieldset id="${ question.id }"
                <% if (question.cssClasses) { %>
                      class="${ question.cssClasses?.join(' ') }"
                <% } %>
                <% if (question.fieldSeparator) { %>
                      field-separator="${ question.fieldSeparator }"
                <% } %>
                <% if (question.displayTemplate) { %>
                      display-template="${ ui.escapeAttribute(question.displayTemplate) }"
                <% } %>>

                <legend>${ ui.message(question.legend) }</legend>
                 <% if(question.header) { %>
                    <h3>${ ui.message(question.header) }</h3>
                 <% } %>

                 <% if (question.id == 'name') { %>
                     <% nameTemplate.lines.each { line ->
                         // go through each line in the template and find the first name token;
                         // assumption is there is only one name token per line
                         def name = line.find({ it['isToken'] == 'IS_NAME_TOKEN' })['codeName'] as String
                         def initialNameFieldValue = ''
                         if (person.personName && person.personName[name]) {
                             initialNameFieldValue = ui.encodeHtml(person.personName[name])
                         } %>
                     ${ui.includeFragment('registrationapp', 'field/personName', [
                             label        : ui.message(nameTemplate.nameMappings[name]),
                             size         : nameTemplate.sizeMappings[name],
                             formFieldName: name,
                             dataItems    : 4,
                             left         : true,
                             ignoreCheckForSimilarNames: true,
                             initialValue : initialNameFieldValue,
                             classes      : [(name == 'givenName' || name == 'familyName') ? 'required' : '']])}
                     <% } %>
                     <input type="hidden" name="preferred" value="true"/>
                <% } else { %>
                    <% fields.each { field ->
                        def configOptions = (field.fragmentRequest.configuration != null)
                                ? field.fragmentRequest.configuration
                                : [:]
                        configOptions.label = ui.message(field.label)
                        configOptions.formFieldName = field.formFieldName
                        configOptions.left = true
                        configOptions.classes = field.cssClasses

                        if (field.type == 'gender') {
                            configOptions.initialValue = person.gender
                            configOptions.options = genderOptions
                        }
                        if (field.type == 'birthdate') {
                            configOptions.estimated = person.birthdateEstimated
                            configOptions.initialValue = person.birthdate
                        }
                        if (field.type == 'personAddress') {
                            configOptions.addressTemplate = addressTemplate
                            configOptions.initialValue = person.personAddress
                        }
                        if(field.type == 'personAttribute'){
                            configOptions.initialValue = ui.escapeAttribute(uiUtils.getAttribute(person, field.uuid))
                        } %>
                        ${ ui.includeFragment(field.fragmentRequest.providerName, field.fragmentRequest.fragmentId, configOptions) }
                    <% } %>
                <% } %>
            </fieldset>
        <% } %>
    </section>

    <div id="confirmation">
        <span id="confirmation_label" class="title">
            ${ ui.message("cfl.registration.confirm.label") }
        </span>
        <div class="before-dataCanvas"></div>
        <div id="dataCanvas"></div>
        <div class="after-data-canvas"></div>
        <div id="confirmationQuestion">
            ${ ui.message("cfl.registration.confirm") }
            <p style="display: inline">
                <button id="registration-submit" type="submit" class="submitButton confirm right">
                    ${ui.message("cfl.registration.confirm.label")}
                </button>
            </p>
            <p style="display: inline">
                <button id="cancelSubmission" class="cancel" type="button">
                    ${ui.message("cfl.registration.cancel")}
                </button>
            </p>
        </div>
    </div>
</form>
