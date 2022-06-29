/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

var delPerson = delPerson || {};

delPerson.deletePersonCreationDialog = null;

/**
 * Functions used to Delete a Person
 */

delPerson.returnUrl = "/owa/cfl/index.html#/find-caregiver";

delPerson.createDeletePersonCreationDialog = function() {
    emr.loadMessages([
        "cfl.deletePerson.success",
        "cfl.deletePerson.failed"
    ]);
    delPerson.deletePersonCreationDialog = emr.setupConfirmationDialog({
        selector: '#delete-person-creation-dialog',
        actions: {
            confirm: function() {
                var deleteReason = jq('#delete-reason').val().trim(); // Retrieve text from text box
                if(deleteReason && deleteReason.length > 0) { // Should not be invalid or empty
                    jq.ajax({
                        url: '/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/person/' + delPerson.personUUID + '?reason='+deleteReason,
                        type: 'DELETE',
                        success: function() {
                            emr.successMessage("cfl.deletePerson.success");
                            jq('#delete-person-creation-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled');
                            delPerson.deletePersonCreationDialog.close();
                            jq(setTimeout(delPerson.goToReturnUrl, 1275)); // Allow the success message to display before redirecting to another page
                        },
                        error: function() {
                            emr.errorMessage("cfl.deletePerson.failed");
                            delPerson.deletePersonCreationDialog.close();
                        }
                    });
                } else {
                    jq('#delete-reason-empty').css({'color' : 'red', display : 'inline'}).show(); // Show warning message if empty
                    jq('#delete-reason').val(""); // Clear the text box
                }
            },
            cancel: function() {
                // Clear fields, close dialog box
                jq('#delete-reason').val("");
                jq('#delete-reason-empty').hide();
                delPerson.deletePersonCreationDialog.close();
            }
        }
    });
};

delPerson.showDeletePersonCreationDialog = function(personUUID) {
    delPerson.personUUID = personUUID;
    if (delPerson.deletePersonCreationDialog == null) {
        delPerson.createDeletePersonCreationDialog();
    }
    jq('#delete-reason-empty').hide();
    delPerson.deletePersonCreationDialog.show();
};

delPerson.goToReturnUrl = function() {
    emr.navigateTo({ applicationUrl: emr.applyContextModel(delPerson.returnUrl)});
};
