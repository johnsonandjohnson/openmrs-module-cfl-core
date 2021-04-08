/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

jq = jQuery;

jq(function () {
    /* Submit functionality */
    jq('#deactivateProgram').submit(function (e) {
        e.preventDefault();
        jq('#submit').attr('disabled', 'disabled');
        jq('#cancelSubmission').attr('disabled', 'disabled');
        jq('#validation-errors').hide();
        var formData = jq('#deactivateProgram').serialize();

        var urlParams = new URLSearchParams(window.location.search);
        var patientProgramId = urlParams.get('patientProgramId');
        var returnUrl = urlParams.get('returnUrl');

        var url = '/' + OPENMRS_CONTEXT_PATH + '/cfl/deactivateProgram/submit.action?patientProgramId=' + patientProgramId + '&returnUrl=' + returnUrl;
        jq.ajax({
            url: url,
            type: 'POST',
            data: formData,
            dataType: 'json',
            success: function (response) {
                emr.navigateTo({'applicationUrl': response.message});
            },
            error: function (response) {
                jq('#validation-errors-content').html(response.responseJSON.globalErrors);
                jq('#validation-errors').show();
                jq('#submit').removeAttr('disabled');
                jq('#cancelSubmission').removeAttr('disabled');
            }
        });
    });
});

