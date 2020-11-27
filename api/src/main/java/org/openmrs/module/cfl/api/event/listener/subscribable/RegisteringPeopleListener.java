package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VisitReminderService;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

public class RegisteringPeopleListener extends PeopleActionListener {

    private WelcomeService welcomeService;
    private VisitReminderService visitReminderService;

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.CREATED.name());
    }

    @Override
    public void performAction(Message message) {
        Person person = extractPerson(message);
        if (person != null) {
            welcomeService.sendWelcomeMessages(person);
            if (getConfigService().isVaccinationInfoIsEnabled()) {
                createFirstVisit(person.getUuid(), getConfigService().getVaccinationProgram(person));
                visitReminderService.create(person);
            }
        }
    }

    private void createFirstVisit(String patientUuid, String vaccinationProgram) {
        Randomization randomization = getConfigService().getRandomizationGlobalProperty();

        Vaccination vaccination = randomization.findByVaccinationProgram(vaccinationProgram);
        VisitInformation visitInformation = vaccination.getVisits().get(0);

        Visit visit = VisitUtil.createVisitResource(Context.getPatientService().getPatientByUuid(patientUuid),
                DateUtil.now(), visitInformation);

        Context.getVisitService().saveVisit(visit);
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }

    public void setWelcomeService(WelcomeService welcomeService) {
        this.welcomeService = welcomeService;
    }

    public void setVisitReminderService(VisitReminderService visitReminderService) {
        this.visitReminderService = visitReminderService;
    }
}
