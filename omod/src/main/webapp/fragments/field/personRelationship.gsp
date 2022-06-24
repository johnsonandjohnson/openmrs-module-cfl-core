<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.6.0.min.js")
    ui.includeJavascript("uicommons", "services/personService.js")
    ui.includeJavascript("cflcore", "field/personRelationship.js")
    ui.includeJavascript("cflcore", "validator/personRelationshipValidator.js")
%>

<div ng-app="personRelationships" ng-controller="PersonRelationshipController"
     ng-init='relationships = ${ groovy.json.JsonOutput.toJson(initialRelationships) }'>

    <span id="person_relationship_field_error_global" class="field-error" style="display: none"></span>

    <div ng-repeat="(index, relationship) in relationships" class="person-relationship-entry">
        <p class="left">
            <select id="{{'relationship_type-' + index}}" name="relationship_type"
                    class="rel_type" ng-model="relationship.type">
                <option value="">${ui.message('registrationapp.person.relationship.selectRelationshipType')}</option>
                <% relationshipTypes.each { relName, relUuid -> %>
                    <option value="${relUuid}">${relName}</option>
                <% } %>
            </select>
        </p>

        <p class="left">
            <input type="text" id="{{'other_person_uuid-' + index}}" class="person-typeahead" placeholder="${ui.message('registrationapp.person.name')}"
                   ng-model="relationship.name"
                   ng-change="onChangedPersonName(\$index)"
                   typeahead="person as person.display for person in getPersons(\$viewValue) | limitTo:5"
                   typeahead-min-length="3"
                   typeahead-on-select="selectPerson(\$item, \$index)"/>
            <input type="text" name="other_person_uuid" ng-model="relationship.uuid" ng-show="false"/>
        </p>

        <p style="padding: 10px">
            <a ng-click="addNewRelationship()">
                <i class="icon-plus-sign edit-action"></i>
            </a>
            <a ng-click="removeRelationship(relationship)">
                <i class="icon-minus-sign edit-action"></i>
            </a>
        </p>
        <span id="{{'person_relationship_field_error-' + index}}" class="field-error" style="display: none"></span>
    </div>

    <% if (person) { %>
        <script>
            jq("#registration-section-form").on("submit", function() {
                var formData = jq('#registration-section-form').serialize();
                formData += "&personId=" + ${ person.personId };
                var url = '/' + OPENMRS_CONTEXT_PATH + '/cfl/field/personRelationship/updateRelationships.action';
                jq.ajax({
                    async: false,
                    url: url,
                    type: "POST",
                    data: formData,
                    dataType: "json",
                    error: function(response) {
                        jq('#submit').removeAttr('disabled');
                        jq('#cancelSubmission').removeAttr('disabled');
                    }
                });
            })
        </script>
    <% } %>
</div>
