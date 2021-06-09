package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.VisitUtil;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

/**
 * The VaccinationEncounterListener class.
 * <p>
 * This listener is responsible for scheduling following visits according to the vaccination program defined in Global
 * Property {@link CFLConstants#VACCINATION_PROGRAM_KEY}.
 * </p>
 * <p>
 * This listener must be enabled via Global Parameter: {@link CFLConstants#VACCINATION_LISTENER_KEY}.
 * </p>
 * <p>
 * The following visits are scheduled based on the Encounters date.
 * </p>
 * <p>
 * The listener observes the creation of an Encounter event and runs it's logic only when:
 * <ul>
 *     <li>the Encounter type is defined in Global Property
 *     {@link CFLConstants#VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY}</li>
 *     <li>the Encounter has visit assigned</li>
 *     <li>the Vaccination information is enabled ({@link CFLConstants#VACCINATION_INFORMATION_ENABLED_KEY} is true) </li>
 *     <li>the Visit has occurred status</li>
 *     <li>the Visit is the last dosage visit scheduled for its patient</li>
 * </ul>
 * </p>
 *
 * @see UpdatingVisitListener
 */
public class VaccinationEncounterListener extends EncounterActionListener {
    public static final String NAME = "VaccinationEncounterListener";

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.CREATED.name());
    }

    @Override
    public void performAction(Message message) {
        // is listener not enabled or is vaccination info not enabled
        if (!getConfigService().isVaccinationListenerEnabled(NAME) || !getConfigService().isVaccinationInfoIsEnabled()) {
            return;
        }

        final Encounter newEncounter = extractEncounter(message);

        if (isVaccinationEncounter(newEncounter) && newEncounter.getVisit() != null) {
            final Visit updatedVisit = newEncounter.getVisit();
            final String visitStatus = VisitUtil.getVisitStatus(updatedVisit);

            if (visitStatus.equals(VisitUtil.getOccurredVisitStatus())) {
                Context
                        .getService(VaccinationService.class)
                        .createFutureVisits(updatedVisit, newEncounter.getEncounterDatetime());
            }
        }
    }

    private boolean isVaccinationEncounter(Encounter newEncounter) {
        return getConfigService().getVaccinationEncounterTypeUUIDs().contains(newEncounter.getEncounterType().getUuid());
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
