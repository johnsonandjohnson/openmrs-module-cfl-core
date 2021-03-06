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

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.PatientService;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * The AOP Advisor which adds advice to the {@link
 * org.openmrs.api.PatientService#getCountOfPatients(String)} which applies location based logic to
 * the patient count.
 */
@OpenmrsProfile(modules = "locationbasedaccess:0.2.* - 0.3.*")
public class PatientCountAdvisor extends StaticMethodMatcherPointcutAdvisor implements Advisor {
  private static final long serialVersionUID = -7809592104506116755L;

  private static final Class<?> SERVICE_CLASS = PatientService.class;
  private static final String METHOD_NAME = "getCountOfPatients";

  public PatientCountAdvisor() {
    super(new PatientLocationBasedAccessCountAdvise());
  }

  @Override
  public boolean matches(Method method, Class<?> aClass) {
    return SERVICE_CLASS.isAssignableFrom(aClass) && METHOD_NAME.equals(method.getName());
  }
}
