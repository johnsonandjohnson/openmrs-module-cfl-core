package org.openmrs.module.cfl.api.event.listener.subscribable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.CFLPersonService;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccinesGlobalPropertyListener extends GlobalPropertyActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccinesGlobalPropertyListener.class);

    private static final String GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME = "cfl.globalPropertyHistoryService";

    private static final String VACCINES_GLOBAL_PROPERTY_NAME = "cfl.vaccines";

    private static final String VISITS_FIELD_NAME = "visits";

    private static final String NUMBER_OF_DOSE_FIELD_NAME = "numberOfDose";

    private static final String CFL_PERSON_SERVICE_BEAN_NAME = "cflPersonService";

    private static final String VACCINATION_SERVICE_BEAN_NAME = "cfl.vaccinationService";

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        GlobalProperty globalProperty = extractGlobalProperty(message);
        if (StringUtils.equals(globalProperty.getProperty(), CFLConstants.VACCINATION_PROGRAM_KEY)) {
            LOGGER.info(String.format("%s global property has been updated", CFLConstants.VACCINATION_PROGRAM_KEY));
            String currentGPValue = globalProperty.getPropertyValue();
            String oldGPValue = getGlobalPropertyHistoryService()
                    .getLastValueOfGlobalProperty(VACCINES_GLOBAL_PROPERTY_NAME);
            Map<String, Boolean> differencesMap = getDifferencesMap(oldGPValue, currentGPValue);
            adjustVisitsBasedOnRegimenChanges(differencesMap);
        }
    }

    private void adjustVisitsBasedOnRegimenChanges(Map<String, Boolean> differencesMap) {
        for (Map.Entry<String, Boolean> entry : differencesMap.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                List<Person> people = getCFLPersonService().findByVaccinationName(entry.getKey());
                for (Person person : people) {
                    rescheduleVisitsByPerson(person);
                }
            }
        }
    }

    private void rescheduleVisitsByPerson(Person person) {
        Patient patient = new Patient(person);
        List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
        if (CollectionUtils.isNotEmpty(visits)) {
            Visit lastOccurredDosingVisit = VisitUtil.getLastOccurredDosingVisit(visits);
            getVaccinationService().rescheduleVisits(lastOccurredDosingVisit, patient);
        }
    }

    private Map<String, Boolean> getDifferencesMap(String previousValue, String newValue) {
        Map<String, Map<String, Object>> previousValuesMap = createVaccinationMap(previousValue);
        Map<String, Map<String, Object>> newValuesMap = createVaccinationMap(newValue);

        Map<String, Boolean> resultMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : newValuesMap.entrySet()) {
            if (previousValuesMap.containsKey(entry.getKey())) {
                Map<String, Object> previousVaccinationMap = previousValuesMap.get(entry.getKey());
                List<VisitInformation> previousVisits =
                        (List<VisitInformation>) previousVaccinationMap.get(VISITS_FIELD_NAME);

                Map<String, Object> newVaccinationMap = entry.getValue();
                List<VisitInformation> newVisits = (List<VisitInformation>) newVaccinationMap.get(VISITS_FIELD_NAME);

                resultMap.put(entry.getKey(), false);
                boolean isNumberOfDoseChanged = isNumberOfDoseChanged(previousVaccinationMap, newVaccinationMap);
                if (previousVisits.size() != newVisits.size() || isNumberOfDoseChanged) {
                    resultMap.put(entry.getKey(), true);
                }

                for (int i = 0; i < newVisits.size(); i++) {
                    if (!newVisits.get(i).equals(previousVisits.get(i))) {
                        resultMap.put(entry.getKey(), true);
                    }
                }
            }
        }
        return resultMap;
    }

    private Map<String, Map<String, Object>> createVaccinationMap(String gpName) {
        Randomization randomization = new Randomization(getGson().fromJson(gpName, Vaccination[].class));
        Map<String, Map<String, Object>> vaccinationValueMap = new HashMap<>();
        for (Vaccination vaccination : randomization.getVaccinations()) {
            Map<String, Object> innerMap = new HashMap<>();
            innerMap.put(VISITS_FIELD_NAME, vaccination.getVisits());
            innerMap.put(NUMBER_OF_DOSE_FIELD_NAME, vaccination.getNumberOfDose());
            vaccinationValueMap.put(vaccination.getName(), innerMap);
        }
        return vaccinationValueMap;
    }

    private boolean isNumberOfDoseChanged(Map<String, Object> previousVaccinationMap,
                                          Map<String, Object> newVaccinationMap) {
        String numberOfDoseFieldName = NUMBER_OF_DOSE_FIELD_NAME;
        return !previousVaccinationMap.get(numberOfDoseFieldName).equals(newVaccinationMap.get(numberOfDoseFieldName));
    }

    private GlobalPropertyHistoryService getGlobalPropertyHistoryService() {
        return Context.getRegisteredComponent(GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class);
    }

    private Gson getGson() {
        return new GsonBuilder().setLenient().create();
    }

    private CFLPersonService getCFLPersonService() {
        return Context.getRegisteredComponent(CFL_PERSON_SERVICE_BEAN_NAME, CFLPersonService.class);
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}
