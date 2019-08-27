package org.openmrs.module.cfl.fragment.controller.manageAccount;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

public class TextFragmentController {

	public void controller(FragmentModel model,
			HttpSession session,
			FragmentConfiguration config,
			@SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
			@RequestParam(value = "userId", required = false) User user,
			@RequestParam(value = "personId", required = false) Person person) {

		ObjectMapper mapper = new ObjectMapper();
		List<Extension> customPersonAttributeEditFragments = getAllExtensions(appFrameworkService);
		model.addAttribute("customPersonAttributeEditFragments", customPersonAttributeEditFragments);
		SimpleObject so = createCustomPersonAttributeJson(config, person, customPersonAttributeEditFragments);
		try {
			model.addAttribute("customPersonAttributeJson", mapper.writeValueAsString(so));
		}
		catch (IOException e) {
			model.addAttribute("customPersonAttributeJson", "{}");
		}


	}

	private SimpleObject createCustomPersonAttributeJson(FragmentConfiguration config, Person person,
			List<Extension> customPersonAttributeEditFragments) {
		SimpleObject so = new SimpleObject();
		for(Extension ext : customPersonAttributeEditFragments) {
			Object type = ext.getExtensionParams().get("type");
			Object personAttributeTypeUuid = ext.getExtensionParams().get("uuid");
			if (type.toString().equals("personAttribute")) {
				String formFieldName = ext.getExtensionParams().get("formFieldName").toString();
				if (person != null && type != null && personAttributeTypeUuid != null) {
					PersonAttribute personAttribute = person.getAttribute(Context.getPersonService()
							.getPersonAttributeTypeByUuid(personAttributeTypeUuid.toString()));
					if(personAttribute == null) {
						PersonAttributeType personAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(personAttributeTypeUuid.toString());
						personAttribute = new PersonAttribute(personAttributeType, ".");
						person.getAttributes().add(personAttribute);
						Context.getPersonService().savePerson(person);
					}
					String personAttributeUuid = personAttribute.getUuid();
					SimpleObject personAttributeInfo = new SimpleObject();
					personAttributeInfo.put("formFieldName", formFieldName);
					personAttributeInfo.put("personAttributeUuid", personAttributeUuid);
					so.put("personAttributeInfo" + formFieldName, personAttributeInfo);
				}
				if (formFieldName.equals(config.getAttribute("formFieldName")) && ext.getExtensionParams().containsKey("config")) {
					config.putAll((Map<? extends String, ?>) ext.getExtensionParams().get("config"));
				}
			}
		}
		return so;
	}

	private List<Extension> getAllExtensions(AppFrameworkService appFrameworkService) {
		List<Extension> customPersonAttributeEditFragments =
				appFrameworkService.getExtensionsForCurrentUser("userAccount.personAttributeEditFragment");
		Collections.sort(customPersonAttributeEditFragments);
		return customPersonAttributeEditFragments;
	}

}
