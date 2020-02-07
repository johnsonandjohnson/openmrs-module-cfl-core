package org.openmrs.module.cfl.extension.builder;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtensionBuilder {

    private AppFrameworkService appFrameworkService;
    private String dashboard;
    private AppContextModel contextModel;

    public ExtensionBuilder(AppFrameworkService appFrameworkService, String dashboard, AppContextModel contextModel) {
        this.appFrameworkService = appFrameworkService;
        this.dashboard = dashboard;
        this.contextModel = contextModel;
    }

    public List<Extension> buildFirstColumn() {
        return sortedExtensions(".firstColumnFragments");
    }

    public List<Extension> buildSecondColumn() {
        return sortedExtensions(".secondColumnFragments");
    }

    public List<Extension> buildOverallActions() {
        return sortedExtensions(".overallActions");
    }

    public List<Extension> buildVisitActions() {
        return contextModel.get("visit") == null ? new ArrayList<Extension>()
                : sortedExtensions(".visitActions");
    }

    public List<Extension> buildOtherActions() {
        List<Extension> otherActions = appFrameworkService.getExtensionsForCurrentUser(
                (StringUtils.equals(dashboard, "patientDashboard") ?
                        "clinicianFacingPatientDashboard" : dashboard) + ".otherActions", contextModel);
        return sorted(otherActions);
    }

    public List<Extension> buildInclude() {
        return sortedExtensions(".includeFragments");
    }

    private List<Extension> sortedExtensions(String dashboardKey) {
        return sorted(appFrameworkService
                .getExtensionsForCurrentUser(dashboard + dashboardKey, contextModel));
    }

    private List<Extension> sorted(List<Extension> extensions) {
        Collections.sort(extensions);
        return extensions;
    }
}
