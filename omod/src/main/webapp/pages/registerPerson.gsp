<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>
<!-- Based on openmrs-module-registrationapp v1.13.0
     omod/src/main/webapp/pages/registerPatient.gsp -->

<%
    if (sessionContext.authenticated && !sessionContext.currentProvider) {
        throw new IllegalStateException('Logged-in user is not a Provider')
    }
    ui.decorateWith('appui', 'standardEmrPage')

    ui.includeJavascript('uicommons', 'handlebars/handlebars.min.js', Integer.MAX_VALUE - 1);
    ui.includeJavascript('uicommons', 'navigator/validators.js', Integer.MAX_VALUE - 19)
    ui.includeJavascript('uicommons', 'navigator/navigator.js', Integer.MAX_VALUE - 20)
    ui.includeJavascript('uicommons', 'navigator/navigatorHandlers.js', Integer.MAX_VALUE - 21)
    ui.includeJavascript('uicommons', 'navigator/navigatorModels.js', Integer.MAX_VALUE - 21)
    ui.includeJavascript('uicommons', 'navigator/navigatorTemplates.js', Integer.MAX_VALUE - 21)
    ui.includeJavascript('uicommons', 'navigator/exitHandlers.js', Integer.MAX_VALUE - 22);

    ui.includeJavascript('cfl', 'registerPerson.js');
    ui.includeCss('registrationapp', 'registerPatient.css')

    def genderOptions = [[label: ui.message("emr.gender.M"), value: 'M'],
                         [label: ui.message("emr.gender.F"), value: 'F']]

    def breadcrumbMiddle = breadcrumbOverride ?: ''

%>

<% if (includeFragments) {
    includeFragments.each { %>
${ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment)}
<% }
} %>

${ui.includeFragment('uicommons', 'validationMessages')}

<script type="text/javascript">

    var breadcrumbs = _.compact(_.flatten([
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        ${ breadcrumbMiddle },
        {
            label: "${ ui.message('cfl.registration.label') }",
            link: "${ ui.pageLink('cfl', 'registerPerson') }"
        }
    ]));

    var testFormStructure = "${formStructure}";
    var appId = '${ui.escapeJs(appId)}';

    var sections = [];
    <% formStructure.sections.each { structure ->
            def section = structure.value;  %>
    sections.push('${section.id}');
    <% } %>

</script>

<div id="validation-errors" class="note-container" style="display: none">
    <div class="note error">
        <div id="validation-errors-content" class="text">
        </div>
    </div>
</div>


<h2>${ui.message(mainTitle)}</h2>

<form class="simple-form-ui" id="registration" method="POST">
    <% formStructure.sections.each { structure ->
        def section = structure.value
        def questions = section.questions
    %>
    <section id="${section.id}" class="non-collapsible">
        <span id="${section.id}_label" class="title">
            ${ui.message(section.label)}
        </span>
        <% questions.each { question ->
            def fields = question.fields
            def classes = question.cssClasses ? question.cssClasses.join(' ') : '' %>
        <fieldset id="${question.id}"
            <% if (classes.length() > 0) { %>
                  class="${classes}"
            <% } %>
            <% if (question.fieldSeparator) { %>
                  field-separator="${question.fieldSeparator}"
            <% } %>
            <% if (question.displayTemplate) { %>
                  display-template="${ui.escapeAttribute(question.displayTemplate)}"
            <% } %>>

            <legend>${ui.message(question.legend)}</legend>
            <% if (question.header) { %>
            <h3>${ui.message(question.header)}</h3>
            <% } %>

            <% if (question.id == 'name') { %>
                <% nameTemplate.lines.each { line ->
                    // go through each line in the template and find the first name token;
                    // assumption is there is only one name token per line
                    def name = line.find({ it['isToken'] == 'IS_NAME_TOKEN' })['codeName'] as String
                    def initialNameFieldValue = ''
                    if (person.personName && person.personName[name]) {
                        initialNameFieldValue = person.personName[name]
                    } %>
                ${ui.includeFragment('registrationapp', 'field/personName', [
                        label        : ui.message(nameTemplate.nameMappings[name]),
                        size         : nameTemplate.sizeMappings[name],
                        formFieldName: name,
                        dataItems    : 4,
                        left         : true,
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
                    }
                    if (field.type == 'personRelationships') {
                        configOptions.relationshipTypes = relationshipTypes
                    } %>
                ${ui.includeFragment(field.fragmentRequest.providerName, field.fragmentRequest.fragmentId, configOptions)}
                <% } %>
            <% } %>
        </fieldset>
        <% } %>
    </section>
    <% } %>

    <div id="confirmation">
        <span id="confirmation_label" class="title">
            ${ui.message('cfl.registration.confirm.label')}
        </span>

        <div class="before-dataCanvas"></div>

        <div id="dataCanvas"></div>

        <div class="after-data-canvas"></div>

        <div id="confirmationQuestion">
            ${ui.message('cfl.registration.confirm')}
            <p style="display: inline">
                <input
                    id="submit"
                    type="submit"
                    class="submitButton confirm right"
                    value="${ui.message('cfl.registration.confirm.label')}"/>
            </p>

            <p style="display: inline">
                <input
                    id="cancelSubmission"
                    class="cancel"
                    type="button"
                    value="${ui.message('cfl.registration.cancel')}"/>
            </p>
        </div>
    </div>
</form>
