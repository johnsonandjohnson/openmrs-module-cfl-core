/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

class PersonRelationshipEntry {

    constructor(rawEntry) {
        this.relationType = jq(rawEntry).find("select");
        this.personName = jq(rawEntry).find("input[id]");
        this.personUuid = jq(rawEntry).find('input[name]');
        this.messageContainer = jq(rawEntry).find("span");
        this.personNameError = "cfl.relatives.personName";
        this.personUuidError = "cfl.relatives.personUuid";
        this.relationshipTypeError = "cfl.relatives.relationshipType";
    }

    isValid() {
        this.messageContainer.empty();
        let valid = true;
        // if type is set but person name is empty
        if (this.relationType.val() && $.trim(this.relationType.val()).length > 0 && this.personName.val() === "") {
            valid = false;
            this.messageContainer.append(emr.message(this.personNameError));
        }
        // if person name is set but type is empty
        if (this.personName.val() && $.trim(this.personName.val()).length > 0 && this.relationType.val() === "") {
            valid = false;
            this.messageContainer.append(emr.message(this.relationshipTypeError));
        }
        // if person name was entered manually not chosen from a drop-down
        if (this.personName.val() && $.trim(this.personName.val()).length > 0 && this.personUuid.val() === "" ) {
            valid = false;
            this.messageContainer.append(" " + emr.message(this.personUuidError));
        }
        if (valid) {
            this.messageContainer.hide();
        } else {
            this.messageContainer.show();
        }
        return valid;
    }
}

function PersonRelationshipValidator() {
    this.messageIdentifier = "requiredField";
}

PersonRelationshipValidator.prototype = new QuestionValidator();
PersonRelationshipValidator.prototype.constructor = PersonRelationshipValidator;
PersonRelationshipValidator.prototype.validate = function(questionModel) {
    let entries = [];
    _.map(jq("div.person-relationship-entry"), function(rawEntry) {
        entries.push(new PersonRelationshipEntry(rawEntry));
    });
    let valid = true;
    valid = _.reduce(entries, function (memo, entry) {
        const isEntryValid = entry.isValid();
        return memo && isEntryValid;
    }, valid);
    if (!valid) {
        return ' ';
    }
    return null;
};

// Updating list of validators to include PersonRelationshipValidator()
if (QuestionValidators) {
    QuestionValidators['person-relationship-validator'] = new PersonRelationshipValidator();
} else {
    console.warn('QuestionValidators object is not defined');
}

jq(function() {
    emr.loadMessages([
        "cfl.relatives.personName",
        "cfl.relatives.personUuid",
        "cfl.relatives.relationshipType"
    ]);
});
