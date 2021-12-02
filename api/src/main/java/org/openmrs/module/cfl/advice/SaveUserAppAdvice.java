package org.openmrs.module.cfl.advice;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.api.model.UserAppContent;
import org.openmrs.module.cfl.api.model.UserAppExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class SaveUserAppAdvice implements AfterReturningAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserAppAdvice.class);

  private static final String SAVE_USER_APP_METHOD_NAME = "saveUserApp";

  private static final String APOSTROPHE_CHAR = "'";

  private static final String APOSTROPHE_CHAR_UNICODE = "&#x27;";

  private static final String SCRIPT_TYPE_NAME = "script";

  @Override
  public void afterReturning(Object o, Method method, Object[] objects, Object o1) {
    if (method.getName().equals(SAVE_USER_APP_METHOD_NAME)) {
      replaceApostropheCharsIfRequired(o);
    }
  }

  private void replaceApostropheCharsIfRequired(Object o) {
    if (o instanceof UserApp) {
      UserApp app = (UserApp) o;
      String appJson = app.getJson();
      StringBuilder sb = new StringBuilder(appJson);
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        UserAppContent userAppContent = objectMapper.readValue(appJson, UserAppContent.class);
        List<UserAppExtension> extensions = getExtensionsWithScriptType(userAppContent);
        if (CollectionUtils.isNotEmpty(extensions)) {
          replaceApostropheChars(sb, extensions);
          setAndSaveUserApp(app, sb.toString());
        }
      } catch (IOException ex) {
        LOGGER.error(
            String.format(
                "Cannot properly map JSON string into %s class", UserAppContent.class.getName()));
      }
    }
  }

  private List<UserAppExtension> getExtensionsWithScriptType(UserAppContent userAppContent) {
    return userAppContent.getExtensions().stream()
        .filter(
            ext ->
                StringUtils.isNotBlank(ext.getScript())
                    && StringUtils.equalsIgnoreCase(ext.getType(), SCRIPT_TYPE_NAME)
                    && ext.getScript().contains(APOSTROPHE_CHAR))
        .collect(Collectors.toList());
  }

  private void replaceApostropheChars(StringBuilder sb, List<UserAppExtension> extensions) {
    for (UserAppExtension extension : extensions) {
      String scriptFieldValue = extension.getScript();
      int startIndex = sb.indexOf(scriptFieldValue);
      int endIndex = startIndex + scriptFieldValue.length();
      scriptFieldValue = scriptFieldValue.replace(APOSTROPHE_CHAR, APOSTROPHE_CHAR_UNICODE);
      sb.replace(startIndex, endIndex, scriptFieldValue);
    }
  }

  private void setAndSaveUserApp(UserApp app, String appJson) {
    app.setJson(appJson);
    Context.getService(AppFrameworkService.class).saveUserApp(app);
  }
}
