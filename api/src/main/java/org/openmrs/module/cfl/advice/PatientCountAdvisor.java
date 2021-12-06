package org.openmrs.module.cfl.advice;

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
