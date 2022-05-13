/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.fragment.controller.personSearch;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.api.dto.PersonOverviewEntryDTO;
import org.openmrs.module.cfl.api.mapper.PersonOverviewMapper;
import org.openmrs.module.cfl.api.util.UserUtil;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsConstants;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Fragment controller for patient search widget; sets the min # of search characters based on global property,
 * and loads last viewed patients for current user if "showLastViewedPatients" fragment config param=true
 *
 * It is based of org.openmrs.module.coreapps.fragment.controller.patientsearch.PatientSearchWidgetFragmentController.
 */
public class PersonSearchWidgetFragmentController {

    @SuppressWarnings("ParameterNumber")
    public void controller(FragmentModel model, UiSessionContext sessionContext, HttpServletRequest request,
                @SpringBean("adminService") AdministrationService administrationService,
                @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                @SpringBean("cfl.personOverviewMapper") PersonOverviewMapper personOverviewMapper,
                @FragmentParam(value = "showLastViewedPatients", required = false) Boolean showLastViewedPatientsParam,
                @FragmentParam(value = "initialSearchFromParameter", required = false) String searchByParam,
                @FragmentParam(value = "registrationAppLink", required = false) String registrationAppLink) {

        boolean showLastViewedPatients = showLastViewedPatientsParam != null ? showLastViewedPatientsParam : false;

        model.addAttribute("minSearchCharacters",
                administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_MIN_SEARCH_CHARACTERS, "1"));

        model.addAttribute("searchDelayShort",
                administrationService.getGlobalProperty(CoreAppsConstants.GP_SEARCH_DELAY_SHORT, "300"));

        model.addAttribute("searchDelayLong",
                administrationService.getGlobalProperty(CoreAppsConstants.GP_SEARCH_DELAY_LONG, "1000"));

        // TODO really should be driven by global property, but currently we only have a property for the java date format
        model.addAttribute("dateFormatJS", "DD MMM YYYY");
        model.addAttribute("locale", Context.getLocale().getLanguage());
        model.addAttribute("defaultLocale", new Locale(administrationService.getGlobalProperty(
                        OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "en"))
                .getLanguage());
        model.addAttribute("dateFormatter", new SimpleDateFormat(
                administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT),
                Context.getLocale()));
        model.addAttribute("searchWidgetDateFormatter", new SimpleDateFormat("yyyy-MM-dd"));
        model.addAttribute("showLastViewedPatients", showLastViewedPatients);

        String doInitialSearch = null;
        if (searchByParam != null && StringUtils.isNotEmpty(request.getParameter(searchByParam))) {
            doInitialSearch = request.getParameter(searchByParam);
        }
        model.addAttribute("doInitialSearch", doInitialSearch);

        if (showLastViewedPatients) {
            List<Person> people = UserUtil.getLastViewedPeople(sessionContext.getCurrentUser());
            model.addAttribute("lastViewedPatients", convertToDTO(people, personOverviewMapper));
        }

        String listingAttributeTypesStr = administrationService.getGlobalProperty(
                OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_LISTING_ATTRIBUTES, "");
        List<String> listingAttributeTypeNames = new ArrayList<>();
        if (StringUtils.isNotBlank(listingAttributeTypesStr)) {
            String[] attTypeNames = StringUtils.split(listingAttributeTypesStr.trim(), ",");
            for (String name : attTypeNames) {
                if (StringUtils.isNotBlank(name.trim())) {
                    listingAttributeTypeNames.add(name.trim());
                }
            }
        }
        model.addAttribute("listingAttributeTypeNames", listingAttributeTypeNames);
        model.addAttribute("registrationAppLink", registrationAppLink);

        List<Extension> patientSearchExtensions = appFrameworkService.getExtensionsForCurrentUser(
                "coreapps.patientSearch.extension");
        Collections.sort(patientSearchExtensions);
        model.addAttribute("patientSearchExtensions", patientSearchExtensions);
    }

    private List<PersonOverviewEntryDTO> convertToDTO(List<Person> people, PersonOverviewMapper mapper) {
        ArrayList<PersonOverviewEntryDTO> results = new ArrayList<PersonOverviewEntryDTO>();
        for (Person person : people) {
            results.add(mapper.toDto(person));
        }
        return results;
    }
}
