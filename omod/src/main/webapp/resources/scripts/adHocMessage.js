/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

/**
 * Handle optional inputs for channel configuration.
 */
$j(document).ready(function () {
    channelCheckboxClicked(document.getElementById("callChannel"), "callConfig");
    channelCheckboxClicked(document.getElementById("smsChannel"), "messageConfig");
    channelCheckboxClicked(document.getElementById("whatsAppChannel"), "messageConfig");

    document.getElementById("send-btn").onclick = function (event) {
        const doSend = confirm($sendConfirmationMessage);
        if (!doSend) {
            event.preventDefault();
        }
    };

    /**
     * Initializes Patient Overview table.
     */
    $j("#patientOverview").dataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": false,
        "bFilter": false,
        "iDisplayLength": 25,
        "aoColumns": [
            {"sClass": "identifierColumn", "bSortable": false},
            {"sClass": "givenNameColumn", "bSortable": false},
            {"sClass": "middleNameColumn", "bSortable": false},
            {"sClass": "familyNameColumn", "bSortable": false},
            {"sClass": "ageColumn", "bSortable": false},
            {"sClass": "genderColumn", "bSortable": false}
        ],
        "aaData": $patients,
        "sDom": '<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
    });
});

function channelCheckboxClicked(checkbox, channelConfigElementId) {
    const channelConfigElement = document.getElementById(channelConfigElementId);
    const inputs = Array.from(
      document.querySelectorAll('input[id=callChannel], input[id=smsChannel], input[id=whatsAppChannel]')
    );

    if (checkbox) {
        if (checkbox.checked) {
            channelConfigElement.style.display = "";
            checkbox.required = true;
            inputs.filter(input => input !== checkbox).forEach(otherInput => otherInput.required = false);
        } else {
            channelConfigElement.style.display = "none";
            checkbox.required = false;
            const isAnyChannelSelected = inputs.find(obj => obj.checked);
            if (!isAnyChannelSelected) {
              inputs.forEach(input => input.required = true);
            }

            displayMessageTemplateAreaWhenAtLeastOneMessageChannelTypeIsSelected(checkbox, channelConfigElement);
        }
    }
}

function displayMessageTemplateAreaWhenAtLeastOneMessageChannelTypeIsSelected(checkbox, channelConfigElement) {
    const messageChannelInputs = Array.from(document.querySelectorAll('input[id=smsChannel], input[id=whatsAppChannel]'));
    if (messageChannelInputs.includes(checkbox)) {
        const isAnyOtherMessageCheckboxSelected = messageChannelInputs.find(obj => obj.checked);
        if (isAnyOtherMessageCheckboxSelected) {
            channelConfigElement.style.display = "";
        } else {
            channelConfigElement.style.display = "none";
        }
    }
}
