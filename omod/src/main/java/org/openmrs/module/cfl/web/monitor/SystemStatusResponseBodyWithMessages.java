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
