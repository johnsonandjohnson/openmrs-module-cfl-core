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

/**
 * Controller copied from coreapps module and adapted to handel the person object
 */
public class PersonDashboardWidgetFragmentController {

    public void controller(
            FragmentConfiguration config, @FragmentParam("app") AppDescriptor app,
            @InjectBeans PatientDomainWrapper patientWrapper,
            @SpringBean("adminService") AdministrationService adminService) {
        ObjectMapper mapper = new ObjectMapper();

        Object person = null;
        person = config.get("person");
        if (person == null ) {
            person = config.get("person");
        }

        ObjectNode appConfig = app.getConfig();
        if (person != null) {
            appConfig.put("patientUuid", ((Person) person).getUuid());
        }

        if (appConfig.get("dateFormat") == null) {
            appConfig.put("dateFormat",
                    adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, "yyyy-MM-dd"));
        }

        appConfig.put("locale", Context.getLocale().toString());
        appConfig.put("language", Context.getLocale().getLanguage().toString());

        Map<String, Object> appConfigMap = mapper.convertValue(appConfig, Map.class);
        config.merge(appConfigMap);
        config.addAttribute("json", appConfig.toString().replace('\"', '\''));
    }
}
