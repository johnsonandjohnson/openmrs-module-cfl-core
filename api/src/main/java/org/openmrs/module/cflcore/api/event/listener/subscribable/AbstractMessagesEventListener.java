/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event.listener.subscribable;

import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.util.Properties;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMessagesEventListener extends BaseListener {

    @Override
    public void onMessage(Message message) {
        try {
            final Properties properties = getProperties(message);
            Daemon.runInDaemonThread(() -> handleEvent(properties), getDaemonToken());
        } catch (JMSException ex) {
            throw new CflRuntimeException("Error during handling Messages event", ex);
        }
    }

    @Override
    public void subscribeSelf() {
        Event.subscribe(getSubject(), this);
    }

    @Override
    public void unsubscribeSelf() {
        Event.unsubscribe(getSubject(), this);
    }

    @Override
    public String getSubscriptionDescription() {
        return "Topic: " + getSubject();
    }

    public abstract String getSubject();

    protected abstract void handleEvent(Properties properties);

    private Properties getProperties(Message message) throws JMSException {
        Map<String, Object> properties = new HashMap<String, Object>();

        // OpenMRS event module uses underneath MapMessage to construct Message. For some reason retrieving properties
        // from Message interface doesn't work and we have to map object to MapMessage.
        MapMessage mapMessage = (MapMessage) message;
        Enumeration<String> propertiesKey = (Enumeration<String>) mapMessage.getMapNames();

        while (propertiesKey.hasMoreElements()) {
            String key = propertiesKey.nextElement();
            properties.put(key, mapMessage.getObject(key));
        }
        return new Properties(properties);
    }
}
