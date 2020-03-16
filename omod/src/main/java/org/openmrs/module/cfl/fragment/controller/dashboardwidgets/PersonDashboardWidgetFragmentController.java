package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;

import java.util.Map;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_PROP;

/**
 * Controller copied from coreapps module and adapted to handel the person object
 */
public class PersonDashboardWidgetFragmentController {
    
    private static final String PATIENT = "patient";
    
    private static final String PATIENT_ID = "patientId";
    
    private static final String PATIENT_UUID = "patientUuid";
    
    private static final String DATE_FORMAT = "dateFormat";
    
    private static final String FORMAT = "yyyy-MM-dd";
    
    private static final String LOCALE = "locale";
    
    private static final String LANGUAGE = "language";
    
    private static final String JSON = "json";
    
    public void controller(
            FragmentConfiguration config, @FragmentParam("app") AppDescriptor app,
            @InjectBeans PatientDomainWrapper patientWrapper,
            @SpringBean("adminService") AdministrationService adminService) {
        ObjectMapper mapper = new ObjectMapper();
        
        Object person = null;
        person = config.get(PERSON_PROP);
        if (person == null) {
            person = config.get(PATIENT);
        }
        if (person == null && config.containsKey(PATIENT_ID) && config.get(PATIENT_ID) != null) {
            person = Context.getPersonService().getPerson((Integer) config.get(PATIENT_ID));
        }
        
        ObjectNode appConfig = app.getConfig();
        
        if (person != null) {
            appConfig.put(PATIENT_UUID, ((Person) person).getUuid());
        }
        
        if (appConfig.get(DATE_FORMAT) == null) {
            appConfig.put(DATE_FORMAT,
                    adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, FORMAT));
        }
        
        appConfig.put(LOCALE, Context.getLocale().toString());
        appConfig.put(LANGUAGE, Context.getLocale().getLanguage().toString());
        
        Map<String, Object> appConfigMap = mapper.convertValue(appConfig, Map.class);
        config.merge(appConfigMap);
        config.addAttribute(JSON, appConfig.toString().replace('\"', '\''));
    }
}
