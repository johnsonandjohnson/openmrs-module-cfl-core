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

/**
 * Based on openmrs-module-registrationapp v1.13.0 omod/src/main/webapp/resources/scripts/registerPatient.js
 */
jq = jQuery;
var NavigatorController;

jq(function () {
    NavigatorController = new KeyboardController();

    /* Submit functionality */
    jq('#registration').submit(function (e) {
        e.preventDefault();
        jq('#submit').attr('disabled', 'disabled');
        jq('#cancelSubmission').attr('disabled', 'disabled');
        jq('#validation-errors').hide();
        var formData = jq('#registration').serialize();

        var url = '/' + OPENMRS_CONTEXT_PATH + '/cfl/registerPerson/submit.action?appId=' + appId;
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

    /* Registration date functionality */
    if (NavigatorController.getQuestionById('registration-date') != null) {  // if retro entry configured
        _.each(NavigatorController.getQuestionById(
            'registration-date').fields,
            function (field) { // registration fields are is disabled by default
                if (field.id != 'checkbox-enable-registration-date') {
                    field.hide();
                }
            });

        jq('#checkbox-enable-registration-date').click(function () {
            if (jq('#checkbox-enable-registration-date').is(':checked')) {
                _.each(NavigatorController.getQuestionById('registration-date').fields, function (field) {
                    if (field.id != 'checkbox-enable-registration-date') {
                        field.hide();
                    }
                });
            } else {
                _.each(NavigatorController.getQuestionById('registration-date').fields, function (field) {
                    if (field.id != 'checkbox-enable-registration-date') {
                        field.show();
                    }
                });
            }
        });
    }
});
