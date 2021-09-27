<%
    config.require("id")
    config.require("label")
    config.require("formFieldName")
    def otherAttributes = ''
    if (config.otherAttributes) {
        config.otherAttributes.each{ attr, val ->
            otherAttributes += (' ' + attr + '="' + val + '"')
        }
    }
    def regexMessage = 'Not a valid!'
    if (config.regexMessage) {
        regexMessage = config.regexMessage
    }

    def createAccount = (account.person.personId == null ? true : false);
    def formName = createAccount ? "accountForm" : "personDetailsForm"
%>
<script type="text/javascript">
    initPersonDetails(${customPersonAttributeJson});
</script>

<label for="${ config.id }">
    ${ ui.message(config.label) }
    <% if (config.required) { %><span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span><% } %>
</label>
<input type="text" id="${ config.id }" name="${ config.formFieldName }"
        ng-init="${ config.formFieldName } = '${ config.initialValue }'"
        ng-model="${ config.formFieldName }"
       <% if (config.classes) { %>class="${ config.classes.join(' ') }" <% } %>
       <% if (config.maxLength) { %> ng-maxlength="${ config.maxLength }" <% } %>
       <% if (config.regex) { %> ng-pattern="${ config.regex }" <% } %>
       <% if (config.required) { %>required<% } %>
        ${otherAttributes}/>
<span ng-show="${formName}.${ config.formFieldName }.\$error.required">${ui.message("adminui.field.required")}</span>
<span ng-show="${formName}.${ config.formFieldName }.\$error.pattern">${ ui.message(regexMessage) }</span>
<% if (config.appendToValueDisplayed) { %><span class="append-to-value">${config.appendToValueDisplayed}</span><% } %>
