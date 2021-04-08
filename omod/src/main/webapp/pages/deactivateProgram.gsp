<%
    ui.decorateWith("appui", "standardEmrPage")

        ui.includeJavascript("uicommons", "angular.min.js")
        ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
        ui.includeJavascript("uicommons", "angular-resource.min.js")
        ui.includeJavascript("uicommons", "angular-common.js")

        ui.includeJavascript('cfl', 'deactivateProgram.js');
%>

${ui.includeFragment("coreapps", "patientHeader", [patient: patient])}

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {
            label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'
        },
        {label: "Deactivate program"}
    ];


</script>

<style>
    #mainDiv {
        margin-left:10px;
    }
}
</style>

<h2>Deactivate program</h2>

<form class="simple-form-ui" id="deactivateProgram" method="POST">
    <div id="mainDiv">
            <div>
                ${ui.includeFragment("uicommons", "field/datetimepicker", [
                        formFieldName: "deactivateProgramDate",
                        label        : "Date",
                        useTime      : false,
                        defaultDate: new Date(),
                        endDate      : new Date()
                ])}
            </div>
            </br>
            <div>
                ${ui.includeFragment("uicommons", "field/textarea", [
                        formFieldName: "deactivateProgramReason",
                        label        : "Reason"
                ])}
            </div>

            <div>
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
