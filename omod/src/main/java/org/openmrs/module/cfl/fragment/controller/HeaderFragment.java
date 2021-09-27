package org.openmrs.module.cfl.fragment.controller;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.coreapps.NameSupportCompatibility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class HeaderFragment {

    protected Map<String, String> getNames(PersonName personName) {

        NameSupportCompatibility nameSupport = Context
                .getRegisteredComponent("coreapps.NameSupportCompatibility", NameSupportCompatibility.class);

        Map<String, String> nameFields = new LinkedHashMap<>();
        List<List<Map<String, String>>> lines = nameSupport.getLines();
        String layoutToken = nameSupport.getLayoutToken();

        // note that the assumption is one one field per "line",
        // otherwise the labels that appear under each field may not render properly
        try {
            for (List<Map<String, String>> line : lines) {
                String nameLabel = "";
                StringBuilder nameLine = new StringBuilder();
                boolean hasToken = false;
                for (Map<String, String> lineToken : line) {
                    if (lineToken.get("isToken").equals(layoutToken)) {
                        String tokenValue = BeanUtils.getProperty(personName, lineToken.get("codeName"));
                        nameLabel = nameSupport.getNameMappings().get(lineToken.get("codeName"));
                        if (StringUtils.isNotBlank(tokenValue)) {
                            hasToken = true;
                            nameLine.append(tokenValue);
                        }
                    } else {
                        nameLine.append(lineToken.get("displayText"));
                    }
                }
                // only display a line if there's a token within it we've been able to resolve
                if (StringUtils.isNotBlank(nameLine.toString()) && hasToken) {
                    nameFields.put(nameLabel, nameLine.toString());
                }
            }
            return nameFields;
        } catch (Exception e) {
            throw new APIException("Unable to generate name fields for patient header", e);
        }
    }
}
