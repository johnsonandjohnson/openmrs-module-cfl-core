package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VisitReminderService;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

public class RegisteringPeopleListener extends PeopleActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisteringPeopleListener.class);

    private WelcomeService welcomeService;
    private VisitReminderService visitReminderService;
    private ConfigService configService;

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.CREATED.name());
    }

    @Override
    public void performAction(Message message) {
        final Person person = extractPerson(message);

        safeSendWelcomeMessages(person);

        if (configService.isVaccinationInfoIsEnabled()) {
            createFirstVisit(person, configService.getVaccinationProgram(person));
            visitReminderService.create(person);
        }
    }

    public void setWelcomeService(WelcomeService welcomeService) {
        this.welcomeService = welcomeService;
    }

    public void setVisitReminderService(VisitReminderService visitReminderService) {
        this.visitReminderService = visitReminderService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    private void safeSendWelcomeMessages(Person person) {
        try {
            welcomeService.sendWelcomeMessages(person);
        } catch (Exception e) {
            LOGGER.error("Failed to send Welcome Message after creation of the Person with UUID: " + person.getUuid(), e);
        }
    }

    private void createFirstVisit(Person person, String vaccinationProgram) {
        final CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(person);

        if (countrySetting.isShouldCreateFirstVisit()) {
            final VisitInformation firstVisitInfo = getFirstVisitInfo(vaccinationProgram);

            final Patient patient = Context.getPatientService().getPatientByUuid(person.getUuid());
            final PersonAttribute locationAttribute =
                    patient.getAttribute(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);

            final Visit firstVisit = VisitUtil.createVisitResource(patient, DateUtil.now(), firstVisitInfo);

            if (locationAttribute != null) {
                firstVisit.setLocation(Context.getLocationService().getLocationByUuid(locationAttribute.getValue()));
            } else {
                firstVisit.setLocation(patient.getPatientIdentifier().getLocation());
            }

            Context.getVisitService().saveVisit(firstVisit);
        }
    }

    private VisitInformation getFirstVisitInfo(String vaccinationProgram) {
        Randomization randomization = configService.getRandomizationGlobalProperty();
        Vaccination vaccination = randomization.findByVaccinationProgram(vaccinationProgram);
        if (vaccination != null) {
            return vaccination.getVisits().get(0);
        } else {
            throw new IllegalArgumentException(String.format("Vaccination for %s program not found", vaccinationProgram));
        }

    }
}
