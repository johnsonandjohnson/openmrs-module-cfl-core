package org.openmrs.module.cfl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
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
	public void started() {
		log.info("Started CFL Module");
		try {
			setupHtmlForms();
		}
		catch (Exception e) {
			Module mod = ModuleFactory.getModuleById(CFLConstants.MODULE_ID);
			ModuleFactory.stopModule(mod);
			throw new RuntimeException("failed to setup the required modules", e);
		}
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown CFL Module");
	}
	
	private void setupHtmlForms() throws Exception {
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		FormService formService = Context.getFormService();
		HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

		List<String> htmlforms = Arrays.asList("cfl:htmlforms/cfl-HIV.xml");

		for (String htmlform : htmlforms) {
			HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
		}
	}

}
