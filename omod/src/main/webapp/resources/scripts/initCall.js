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
                    url: `/${OPENMRS_CONTEXT_PATH}/ws/callflows/person/${initCall.personUUID}/out/${initCall.ivrProvider}/flows/MainFlow.ccxml?actorType=${initCall.actorType}`,
                    type: 'GET',
                    success: function() {
                        emr.successMessage("cfl.initCall.success");
                        initCall.initCallCreationDialog.close();
                    },
                    error: function(data) {
                        emr.errorMessage("cfl.initCall.failed");
                        initCall.initCallCreationDialog.close();
                        if (data.status == 403) {
                            window.location.href = `/${OPENMRS_CONTEXT_PATH}/login.htm`;
                        }
                    },
                    final: function() {
                        initCall.enableConfirmButton();
                    }
                });
            },
            cancel: function() {
                initCall.initCallCreationDialog.close();
            }
        }
    });
};

initCall.showInitCallCreationDialog = function(personUUID, actorType, ivrProvider) {
    initCall.personUUID = personUUID;
    initCall.actorType = actorType;
    initCall.ivrProvider = ivrProvider;
    if (initCall.initCallCreationDialog == null) {
        initCall.createInitCallCreationDialog();
    }
    initCall.initCallCreationDialog.show();
};

initCall.goToReturnUrl = function() {
    emr.navigateTo({ applicationUrl: emr.applyContextModel(initCall.returnUrl)});
};

initCall.enableConfirmButton = function() {
    jq('#init-call-dialog' + ' .icon-spin').css('display', 'inline-block').parent().addClass('enabled');
};
