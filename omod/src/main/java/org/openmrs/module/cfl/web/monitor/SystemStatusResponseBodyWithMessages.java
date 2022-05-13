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

import java.util.List;

/**
 * The SystemStatusResponseBody Class represents a variant of body of HTTP response of
 * {@link org.openmrs.module.cfl.web.controller.MonitoringController#getSystemStatus(String, String)} method.
 * <p>
 * Use {@link SystemStatusResponseBodyBuilder}.
 * </p>
 */
public class SystemStatusResponseBodyWithMessages extends SystemStatusResponseBodyStatusOnly {
    private final List<Message> messages;

    SystemStatusResponseBodyWithMessages(final String status, final List<Message> messages) {
        super(status);
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    /**
     * The single component monitoring message.
     */
    static class Message {
        private final String component;
        private final String text;

        Message(final String component, final String text) {
            this.component = component;
            this.text = text;
        }

        public String getComponent() {
            return component;
        }

        public String getText() {
            return text;
        }
    }
}
