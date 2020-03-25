var initCall = initCall || {};

initCall.initCallCreationDialog = null;

/**
 * Functions used to initiate a call
 */

initCall.returnUrl = "/cfl/findPerson.page?app=cfl.findPerson";

initCall.createInitCallCreationDialog = function() {
    emr.loadMessages([
        "cfl.initCall.success",
        "cfl.initCall.failed"
    ]);
    initCall.initCallCreationDialog = emr.setupConfirmationDialog({
        selector: '#init-call-dialog',
        actions: {
            confirm: function() {
                jq.ajax({
                    url: `/${OPENMRS_CONTEXT_PATH}/ws/callflows/person/${initCall.personUUID}/out/voxeo-mobile/flows/MainFlow.ccxml?actorType=${initCall.actorType}`,
                    type: 'GET',
                    success: function() {
                        emr.successMessage("cfl.initCall.success");
                        jq('#init-call-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('disabled');
                        initCall.initCallCreationDialog.close();
                    },
                    error: function() {
                        emr.errorMessage("cfl.initCall.failed");
                        initCall.initCallCreationDialog.close();
                    }
                });
            },
            cancel: function() {
                initCall.initCallCreationDialog.close();
            }
        }
    });
};

initCall.showInitCallCreationDialog = function(personUUID, actorType) {
    initCall.personUUID = personUUID;
    initCall.actorType = actorType;
    if (initCall.initCallCreationDialog == null) {
        initCall.createInitCallCreationDialog();
    }
    initCall.initCallCreationDialog.show();
};

initCall.goToReturnUrl = function() {
    emr.navigateTo({ applicationUrl: emr.applyContextModel(initCall.returnUrl)});
};
