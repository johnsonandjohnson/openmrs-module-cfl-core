package org.openmrs.module.cfl.advice;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class SaveUserAppAdvice implements AfterReturningAdvice {

    private static final String SAVE_USER_APP_METHOD_NAME = "saveUserApp";

    private static final String APOSTROPHE_CHAR = "'";

    private static final String APOSTROPHE_CHAR_UNICODE = "&#x27;";

    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) {
        if (method.getName().equals(SAVE_USER_APP_METHOD_NAME)) {
            replaceApostropheChars(o);
        }
    }

    private void replaceApostropheChars(Object o) {
        if (o instanceof UserApp) {
            UserApp obj = (UserApp) o;
            String appJson = obj.getJson();
            if (appJson.contains(APOSTROPHE_CHAR)) {
                appJson = appJson.replace(APOSTROPHE_CHAR, APOSTROPHE_CHAR_UNICODE);
                obj.setJson(appJson);
                Context.getService(AppFrameworkService.class).saveUserApp(obj);
            }
        }
    }
}
