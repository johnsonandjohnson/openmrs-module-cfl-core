/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.cfldistribution.fragment.controller;

import org.openmrs.module.cfldistribution.CfldistributionWebConstants;
import org.openmrs.ui.framework.fragment.FragmentModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class InfoAndErrorMessagesFragmentController {
	
	public void controller(HttpServletRequest request, FragmentModel fragmentModel) {
		HttpSession session = request.getSession();
		String errorMessage = (String) session
		        .getAttribute(CfldistributionWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE);
		String infoMessage = (String) session.getAttribute(CfldistributionWebConstants.SESSION_ATTRIBUTE_INFO_MESSAGE);
		session.setAttribute(CfldistributionWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, null);
		session.setAttribute(CfldistributionWebConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, null);
		fragmentModel.addAttribute("errorMessage", errorMessage);
		fragmentModel.addAttribute("infoMessage", infoMessage);
	}
	
}
