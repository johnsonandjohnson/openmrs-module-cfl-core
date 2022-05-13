/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.validation.ValidationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * It is based on org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController.
 */
@RequestMapping("/cfl")
public class BaseCflModuleRestController {

    private static final int DEFAULT_ERROR_CODE = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    private static final String DISABLE_WWW_AUTH_HEADER_NAME = "Disable-WWW-Authenticate";

    private static final String DEFAULT_ERROR_DETAIL = "";

    private final Log log = LogFactory.getLog(getClass());

    /**
     * @should return unauthorized if not logged in
     * @should return forbidden if logged in
     */
    @ExceptionHandler(APIAuthenticationException.class)
    @ResponseBody
    public SimpleObject apiAuthenticationExceptionHandler(Exception ex, HttpServletRequest request,
                                                          HttpServletResponse response) {
        int errorCode;
        String errorDetail;
        if (Context.isAuthenticated()) {
            // user is logged in but doesn't have the relevant privilege -> 403 FORBIDDEN
            errorCode = HttpServletResponse.SC_FORBIDDEN;
            errorDetail = "User is logged in but doesn't have the relevant privilege";
        } else {
            // user is not logged in -> 401 UNAUTHORIZED
            errorCode = HttpServletResponse.SC_UNAUTHORIZED;
            errorDetail = "User is not logged in";
            if (shouldAddWWWAuthHeader(request)) {
                response.addHeader("WWW-Authenticate", "Basic realm=\"OpenMRS at " + RestConstants.URI_PREFIX + "\"");
            }
        }
        response.setStatus(errorCode);
        return RestUtil.wrapErrorResponse(ex, errorDetail);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public SimpleObject validationExceptionHandler(ValidationException validationException, HttpServletRequest request,
                                                   HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return RestUtil.wrapValidationErrorResponse(validationException);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SimpleObject handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(ex.getMessage(), ex);
        int errorCode = DEFAULT_ERROR_CODE;
        String errorDetail = DEFAULT_ERROR_DETAIL;
        ResponseStatus ann = ex.getClass().getAnnotation(ResponseStatus.class);
        if (ann != null) {
            errorCode = ann.value().value();
            if (StringUtils.isNotEmpty(ann.reason())) {
                errorDetail = ann.reason();
            }

        } else if (RestUtil.hasCause(ex, APIAuthenticationException.class)) {
            return apiAuthenticationExceptionHandler(ex, request, response);
        } else if (ex.getClass() == HttpRequestMethodNotSupportedException.class) {
            errorCode = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
        }
        response.setStatus(errorCode);
        return RestUtil.wrapErrorResponse(ex, errorDetail);
    }

    private boolean shouldAddWWWAuthHeader(HttpServletRequest request) {
        return request.getHeader(DISABLE_WWW_AUTH_HEADER_NAME) == null ||
                !request.getHeader(DISABLE_WWW_AUTH_HEADER_NAME).equals("true");
    }
}
