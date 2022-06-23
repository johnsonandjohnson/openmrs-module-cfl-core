/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.CFLPatientService;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * The MethodInterceptor Advice which replaces the result of {@link
 * org.openmrs.api.PatientService#getCountOfPatients(String)} with a count of patients limited by
 * currently logged user's assigned locations.
 *
 * @see CFLPatientService#getCountOfPatients(String)
 */
public class PatientLocationBasedAccessCountAdvise implements MethodInterceptor {
  private static final int QUERY_ARG = 0;
  private static final int INCLUDE_VOIDED_ARG = 1;

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    final Object[] methodArguments = methodInvocation.getArguments();

    if (isQueryOnly(methodArguments)) {
      return getCountOfPatients((String) methodArguments[QUERY_ARG]);
    } else if (isQueryAndIncludeVoided(methodArguments)) {
      return getCountOfPatients(
          (String) methodArguments[QUERY_ARG], (Boolean) methodArguments[INCLUDE_VOIDED_ARG]);
    } else {
      throw newUnsupportedMethodException(methodInvocation);
    }
  }

  private boolean isQueryOnly(Object[] methodArguments) {
    return methodArguments.length == 1;
  }

  private boolean isQueryAndIncludeVoided(Object[] methodArguments) {
    return methodArguments.length == 2;
  }

  private Integer getCountOfPatients(String query) {
    return Context.getService(CFLPatientService.class).getCountOfPatients(query).intValue();
  }

  private Integer getCountOfPatients(String query, boolean includeVoided) {
    return Context.getService(CFLPatientService.class)
        .getCountOfPatients(query, includeVoided)
        .intValue();
  }

  private Throwable newUnsupportedMethodException(MethodInvocation methodInvocation) {
    return new APIException(
        MessageFormat.format(
            "Unsupported method for advice PatientService#getCountOfPatients. Method "
                + "{0}, args: {1} ",
            methodInvocation.getMethod().getName(),
            Arrays.toString(methodInvocation.getArguments())));
  }
}
