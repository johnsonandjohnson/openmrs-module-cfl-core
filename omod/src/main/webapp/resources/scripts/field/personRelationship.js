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

        // this is a (hack?) that provides integration with the one-question-per-screen navigator, since the navigator doesn't play well with angular
        // specifically, we override the "displayValue" function on the relationship_type field within the navigator so that:
        // 1) the checkmark in the left-hand navigation of the is properly rendered when data is filled out
        // 2) the confirmation screen at the end of the workflow properly displays the relationships that have been entered
        function configureRelationshipDisplayField() {
            if (typeof(NavigatorController) != 'undefined') {
                var field = NavigatorController.getFieldById("relationship_type");
                field.displayValue = function() {
                    return $scope.relationships.map(function(r) {
                        return r.name +  " - " + jq('.rel_type:first').children("[value='" + r.type + "']").text();
                    }).join(', ');
                }
            }
        }
    }]);
