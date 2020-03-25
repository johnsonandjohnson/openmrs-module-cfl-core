angular.module('personRelationships', ['personService', 'ui.bootstrap'])
    .controller('PersonRelationshipController', ['$scope', 'PersonService', function ($scope, PersonService) {

        $scope.relationships = [{uuid: '', name: '', type: ''}];

        $scope.getPersons = function (searchString) {
            return PersonService.getPersons({'q': searchString, 'v': 'full'});
        };

        $scope.addNewRelationship = function () {
            configureRelationshipDisplayField();
            $scope.relationships.push({uuid: '', name: '', type: ''});
        };

        $scope.removeRelationship = function (relationship) {
            configureRelationshipDisplayField();
            if ($scope.relationships.length > 1) {
                $scope.relationships.splice($scope.relationships.indexOf(relationship), 1);
            } else {
                jq(function () {
                    $scope.relationships[0].uuid = '';
                    jq(".rel_type").val('');
                    jq(".person-typeahead").val('');
                    jq(".person-typeahead").removeClass('ng-touched');
                    jq(".person-typeahead").removeClass('ng-invalid');
                    jq("[name='other_person_uuid']").val('');
                });
            }
        };

        $scope.selectPerson = function (person, index) {
            configureRelationshipDisplayField();
            $scope.relationships[index].uuid = person.uuid;
            $scope.relationships[index].name = person.display;
        };

        $scope.onChangedPersonName = function (index) {
            $scope.relationships[index].uuid = null;
        };

        configureRelationshipDisplayField();

        function configureRelationshipDisplayField() {
            if (typeof (NavigatorController) != 'undefined') {
                let isFirstField = true;
                angular.forEach(NavigatorController.getFields(), function (field) {
                    if (shouldOverrideDisplay(field)) {
                        if (isFirstField) {
                            isFirstField = false;
                            overrideFirstField(field);
                        } else {
                            cleanupOtherFields(field);
                        }
                    }
                });
            }
        }

        function overrideFirstField(field) {
            field.displayValue = function () {
                return $scope.relationships.map(function (r) {
                    if (r.uuid) {
                        return (r.uuid !== "") ? r.name + " - " + jq('.rel_type:first').children("[value='" + r.type + "']").text() : "";
                    } else {
                        return "--";
                    }
                }).join(', ');
            };
        }

        function cleanupOtherFields(field) {
            field.displayValue = function () {
                return "";
            };
        }

        function shouldOverrideDisplay(field) {
            return field.id && field.id.includes("relationship_type")
                || field.id.includes("other_person_uuid");
        }
    }]);
