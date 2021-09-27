package org.openmrs.module.cfl.extension.builder;

import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;

import java.util.Collections;
import java.util.List;

class ExtensionBuilder {

    private AppFrameworkService appFrameworkService;
    private String dashboard;
    private AppContextModel contextModel;

    ExtensionBuilder(AppFrameworkService appFrameworkService, String dashboard, AppContextModel contextModel) {
        this.appFrameworkService = appFrameworkService;
        this.dashboard = dashboard;
        this.contextModel = contextModel;
    }

    List<Extension> sortedExtensions(String dashboardKey) {
        return sorted(appFrameworkService
                .getExtensionsForCurrentUser(dashboard + "." + dashboardKey, contextModel));
    }

    List<Extension> sorted(List<Extension> extensions) {
        Collections.sort(extensions);
        return extensions;
    }

    AppFrameworkService getAppFrameworkService() {
        return appFrameworkService;
    }

    String getDashboard() {
        return dashboard;
    }

    AppContextModel getContextModel() {
        return contextModel;
    }
}
