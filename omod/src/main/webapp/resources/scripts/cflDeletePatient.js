var cflDelPatient = cflDelPatient || {};

cflDelPatient.deletePatientCreationDialog = null;

cflDelPatient.createDeletePatientCreationDialog = function() {
  emr.loadMessages([
    "coreapps.task.deletePatient.deletePatientSuccessful",
    "coreapps.task.deletePatient.deletePatientUnsuccessful"
  ]);
  cflDelPatient.deletePatientCreationDialog = emr.setupConfirmationDialog({
    selector: '#cfl-delete-patient-creation-dialog',
    actions: {
      confirm: function() {
        var deleteReason = jq('#cfl-delete-reason').val().trim();
        if (deleteReason && deleteReason.length > 0) {
          jq.ajax({
            url: '/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/patient/'+ cflDelPatient.patientUUID + '?reason=' + deleteReason,
            type: 'DELETE',
            success: function() {
              emr.successMessage('coreapps.task.deletePatient.deletePatientSuccessful');
              jq('#cfl-delete-patient-creation-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled');
              cflDelPatient.deletePatientCreationDialog.close();
              jq(setTimeout(cflDelPatient.goToReturnUrl, 1000));
            },
            error: function() {
              emr.errorMessage("coreapps.task.deletePatient.deletePatientUnsuccessful");
              cflDelPatient.deletePatientCreationDialog.close();
            }
          });
        } else {
            jq('#cfl-delete-reason-empty').css({'color' : 'red', display : 'inline'}).show();
            jq('#cfl-delete-reason').val("");
        }
      },
      cancel: function() {
        jq('#cfl-delete-reason').val("");
        jq('#delete-reason-empty').hide();
        cflDelPatient.deletePatientCreationDialog.close();
      }
    }
  });
};

cflDelPatient.showDeletePatientCreationDialog = function(patientUUID, returnUrl) {
  cflDelPatient.patientUUID = patientUUID;
  cflDelPatient.returnUrl = returnUrl;
  if (cflDelPatient.deletePatientCreationDialog == null) {
    cflDelPatient.createDeletePatientCreationDialog();
  }
  jq('#cfl-delete-reason-empty').hide();
  cflDelPatient.deletePatientCreationDialog.show();
};

cflDelPatient.goToReturnUrl = function() {
  emr.navigateTo({ applicationUrl: emr.applyContextModel(cflDelPatient.returnUrl)});
};