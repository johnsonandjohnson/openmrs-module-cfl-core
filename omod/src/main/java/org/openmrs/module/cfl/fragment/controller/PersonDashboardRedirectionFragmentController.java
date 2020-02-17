package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PersonDashboardRedirectionFragmentController {

    private static final String SHOW_INFORMATION_ABOUT_ACTOR_DASHBOARD = "showInformationAboutActorDashboard";

    private static final String PERSON_SERVICE = "personService";

    private static final String IS_PATIENT_DASHBOARD = "isPatientDashboard";

    private static final String ACTOR_TYPE_NAME = "actorTypeName";

    private static final String ACTOR_UUID = "actorUuid";

    public void controller(
            @SpringBean(PERSON_SERVICE) PersonService personService,
            @FragmentParam(IS_PATIENT_DASHBOARD) boolean isPatientDashboard,
            @FragmentParam(ACTOR_UUID) String actorUuid,
            FragmentModel model) {

        model.addAttribute(ACTOR_UUID, actorUuid);
        model.addAttribute(IS_PATIENT_DASHBOARD, isPatientDashboard);
        model.addAttribute(SHOW_INFORMATION_ABOUT_ACTOR_DASHBOARD,
                shouldShowActorDashboardRedirection(actorUuid, personService));
        model.addAttribute(ACTOR_TYPE_NAME, getActorTypeName());
    }

    /**
     * Verify if the information panel about existing another actor dashboard should be displayed.
     *
     * @param actorUuid - uuid of related person
     * @param personService - person service
     * @return - flag used to show/hide information panel
     */
    private boolean shouldShowActorDashboardRedirection(String actorUuid, PersonService personService) {
        Person person = personService.getPersonByUuid(actorUuid);
        if (person == null || !person.isPatient()) {
            return false;
        }
        String supportedActorType = getSupportedActorType();
        for (Relationship relationship : personService.getRelationshipsByPerson(person)) {
            if (relationship.getRelationshipType().getUuid().equals(supportedActorType)
                    && relationship.getPersonA().getId().equals(person.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of global property
     * @return - supported actor type setting
     */
    private String getSupportedActorType() {
         return Context.getAdministrationService().getGlobalProperty(CFLConstants.SUPPORTED_ACTOR_TYPE);
     }

    /**
     * Returns the value of global property
     * @return - supported actor type name setting
     */
    private String getActorTypeName() {
        return Context.getAdministrationService().getGlobalProperty(CFLConstants.SUPPORTED_ACTOR_TYPE_NAME);
    }
}
