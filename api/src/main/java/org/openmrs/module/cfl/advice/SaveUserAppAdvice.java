package org.openmrs.module.cfl.advice;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.domain.UserApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The SaveUserAppAdvice class.
 *
 * <p>The implemented method is invoked before calling saveUserApp method from AppFrameworkService.
 * The main reason of this method is replacing apostrophe sign (') with XML-encoded representation
 * in app extensions of script type. This is made because of different UI pages read differently
 * these signs.
 */
public class SaveUserAppAdvice implements MethodBeforeAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserAppAdvice.class);

  private static final String SAVE_USER_APP_METHOD_NAME = "saveUserApp";

  private static final String APOSTROPHE_CHAR = "'";

  private static final String APOSTROPHE_CHAR_UNICODE = "&#x27;";

  private static final String SCRIPT_TYPE_NAME = "script";

  @Override
  public void before(Method method, Object[] objects, Object o) throws Throwable {
    if (method.getName().equals(SAVE_USER_APP_METHOD_NAME)) {
      replaceApostropheCharsIfRequired((UserApp) objects[0]);
    }
  }

  private void replaceApostropheCharsIfRequired(UserApp app) {
    try {
      AppDescriptor appDescriptor = getObjectMapper().readValue(app.getJson(), AppDescriptor.class);
      List<Extension> extensions = getExtensionWithScriptType(appDescriptor);
      if (CollectionUtils.isNotEmpty(extensions)) {
        replaceApostropheChars(extensions);
        app.setJson(
            getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(appDescriptor));
      }
    } catch (IOException ex) {
      LOGGER.error(
          String.format(
              "Cannot properly map JSON string into %s class", AppDescriptor.class.getName()));
    }
  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    return objectMapper;
  }

  private List<Extension> getExtensionWithScriptType(AppDescriptor appDescriptor) {
    return appDescriptor.getExtensions().stream()
        .filter(ext -> StringUtils.isNotBlank(ext.getScript()))
        .filter(ext -> StringUtils.equalsIgnoreCase(ext.getType(), SCRIPT_TYPE_NAME))
        .filter(ext -> ext.getScript().contains(APOSTROPHE_CHAR))
        .collect(Collectors.toList());
  }

  private void replaceApostropheChars(List<Extension> extensions) {
    for (Extension extension : extensions) {
      String scriptFieldValue = extension.getScript();
      extension.setScript(scriptFieldValue.replace(APOSTROPHE_CHAR, APOSTROPHE_CHAR_UNICODE));
    }
  }
}
