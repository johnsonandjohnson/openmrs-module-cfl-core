package org.openmrs.module.cfl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleException;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.api.util.AppFrameworkConstants;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CFLModuleActivator extends BaseModuleActivator {


	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		log.info("Started CFL Module");
		try {
			setupHtmlForms();
			createGlobalSettingIfNotExists(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME,
					CFLConstants.PERSONATTRIBUTETYPE_UUID);
			createGlobalSettingIfNotExists(CFLConstants.DISABLED_CONTROL_KEY,
					CFLConstants.DISABLED_CONTROL_DEFAULT_VALUE, CFLConstants.DISABLED_CONTROL_DESCRIPTION);
			configureDistribution();
			installMetadataPackages();
		}
		catch (Exception e) {
			Module mod = ModuleFactory.getModuleById(CFLConstants.MODULE_ID);
			ModuleFactory.stopModule(mod);
			throw new ModuleException("failed to setup the required modules", e);
		}
	}

	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown CFL Module");
	}

	private void configureDistribution() {
		AdministrationService administrationService = Context.getService(AdministrationService.class);
		AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
		if (CFLConstants.TRUE.equalsIgnoreCase(
				administrationService.getGlobalProperty(CFLConstants.DISABLED_CONTROL_KEY))) {
			enableAdditionalConfiguration(appFrameworkService);
		} else {
			enableDefaultConfiguration(appFrameworkService);
		}
	}

	private void enableAdditionalConfiguration(AppFrameworkService appFrameworkService) {
		enableApps(appFrameworkService, AppFrameworkConstants.CFL_ADDITIONAL_MODIFICATION_APP_IDS);
		disableApps(appFrameworkService, AppFrameworkConstants.APP_IDS);
		disableExtensions(appFrameworkService, AppFrameworkConstants.EXTENSION_IDS);
	}

	private void enableDefaultConfiguration(AppFrameworkService appFrameworkService) {
		disableApps(appFrameworkService, AppFrameworkConstants.CFL_ADDITIONAL_MODIFICATION_APP_IDS);
	}

	private void enableApps(AppFrameworkService service, List<String> appIds) {
		for (String app : appIds) {
			service.enableApp(app);
		}
	}

	private void disableApps(AppFrameworkService service, List<String> appIds) {
		for (String app : appIds) {
			service.disableApp(app);
		}
	}

	private void disableExtensions(AppFrameworkService service, List<String> extensions) {
		for (String ext : extensions) {
			service.disableExtension(ext);
		}
	}

	private void createGlobalSettingIfNotExists(String key, String value) {
		createGlobalSettingIfNotExists(key, value, null);
	}

		private void createGlobalSettingIfNotExists(String key, String value, String description) {
		String existSetting = Context.getAdministrationService().getGlobalProperty(key);
		if (StringUtils.isBlank(existSetting)) {
			GlobalProperty gp = new GlobalProperty(key, value, description);
			Context.getAdministrationService().saveGlobalProperty(gp);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Message Module created '%s' global property with value - %s", key, value));
			}
		}
	}

	private void setupHtmlForms() throws Exception {
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		FormService formService = Context.getFormService();
		HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

		List<String> htmlforms = Arrays.asList("cfl:htmlforms/cfl-HIV.xml", "cfl:htmlforms/cfl-check-in.xml");

		for (String htmlform : htmlforms) {
			HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
		}
	}

	private void installMetadataPackages() {
		try {
			MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
		} catch(Exception e) {
			throw new ModuleException("Failed to load Metadata sharing packages", e);
		}
	}
}
