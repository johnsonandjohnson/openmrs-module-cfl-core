<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    def patient = config.patient
    def MAX_ELEMENTS_NUMBER_IN_COLUMN = 4
    def extraTitleInfoText = ""

    ui.includeCss("cflcore", "patientHeader.css")
    ui.includeJavascript("coreapps", "custom/deletePatient.js")
    ui.includeJavascript("messages", "changeStatus.js")
%>

<head>
  <script src="https://momentjs.com/downloads/moment.js"></script>
</head>

<div class="header-container">
  <div class="title-container">
    <span class="title-field">
      <% patientHeaderConfigDTO.titleFieldDTOs.eachWithIndex { mainTitleField, index -> %>
        <% if(mainTitleField.mainTitleField) { %>
         ${mainTitleField.value}
        <% } %>
      <% } %>
    </span>

     <% if(isExtraTitleFieldsAvailable) { %>
       <% extraTitleInfoText = extraTitleInfoText + "(" %>
     <% } %>
     <% patientHeaderConfigDTO.titleFieldDTOs.eachWithIndex { extraTitleField, index -> %>
      <% if(!extraTitleField.mainTitleField) { %>
        <% extraTitleInfoText = extraTitleInfoText + extraTitleField.value %>
        <% if(index + 1 < patientHeaderConfigDTO.titleFieldDTOs.size()) { %>
          <% extraTitleInfoText = extraTitleInfoText + "/" %>
        <% } %>
      <% } %>
     <% } %>
     <% if(isExtraTitleFieldsAvailable) { %>
      <%
        extraTitleInfoText = extraTitleInfoText.endsWith("/") ? extraTitleInfoText[0..-2] : extraTitleInfoText
        extraTitleInfoText = extraTitleInfoText + ")"
      %>
     <% } %>

     <span class="title-field">
       ${extraTitleInfoText}
     </span>

     <span class="buttons-span">
      <% if (isPatientDashboard && patientHeaderConfigDTO.deleteButtonOnPatientDashboardVisible) { %>
        <button class="btn btn-danger" onclick="delPatient.showDeletePatientCreationDialog('${patient.uuid}')">
          ${ui.message("cfl.deletePatientButton.label")}
        </button>
      <% } %>
      <% if (patientHeaderConfigDTO.updateStatusButtonVisible) { %>
        <button class="btn btn-secondary" onclick="changeStatus.showPersonStatusUpdateDialog('${patient.uuid}')">
          ${ui.message("cfl.updateStatusButton.label")}
        </button>
      <% } %>
     </span>
  </div>

  <div class="fields-container">
    <div class="column">
      <% patientHeaderConfigDTO.attributeFieldDTOs.eachWithIndex { attrField, index -> %>
        <div class="field">
          <span class="label-field">${ui.message(attrField.label)}: </span>
          <span id="attrField-${index}" class="value-field">${attrField.value}</span>
          <% if (attrField.type == 'date') { %>
            <script>
              jq('#attrField-${index}').text(moment.utc("${attrField.value}").local().format("${attrField.format}"));
            </script>
          <% } %>
        </div>
        <% if ((index + 1) % MAX_ELEMENTS_NUMBER_IN_COLUMN == 0) { %>
          </div>
          <div class="column">
        <% } %>
      <% } %>
    </div>
  </div>
</div>

<div>${ ui.includeFragment("messages", "patientdashboard/changeStatus") }</div>