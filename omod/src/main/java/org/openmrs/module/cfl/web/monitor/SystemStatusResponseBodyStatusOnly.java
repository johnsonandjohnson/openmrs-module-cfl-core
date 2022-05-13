/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.monitor;

/**
 * The SystemStatusResponseBody Class represents a variant body of HTTP response of
 * {@link org.openmrs.module.cfl.web.controller.MonitoringController#getSystemStatus(String, String)} method.
 * <p>
 * Use {@link SystemStatusResponseBodyBuilder}.
 * </p>
 */
public class SystemStatusResponseBodyStatusOnly implements SystemStatusResponseBody {
    private final String status;

    SystemStatusResponseBodyStatusOnly(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
