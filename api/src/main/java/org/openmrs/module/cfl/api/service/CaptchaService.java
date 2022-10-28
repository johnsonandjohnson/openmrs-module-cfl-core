package org.openmrs.module.cfl.api.service;

import javax.servlet.http.HttpServletRequest;

public interface CaptchaService {
	
	void processResponse(HttpServletRequest request);
}