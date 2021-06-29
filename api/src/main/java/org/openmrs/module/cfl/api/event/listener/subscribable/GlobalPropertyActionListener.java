package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.GlobalProperty;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class GlobalPropertyActionListener extends BaseActionListener {

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        List<Class<? extends OpenmrsObject>> list = new ArrayList<Class<? extends OpenmrsObject>>();
        list.add(GlobalProperty.class);
        return list;
    }

    protected GlobalProperty extractGlobalProperty(Message message) {
        String globalPropertyUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        return getGlobalProperty(globalPropertyUuid);
    }

    private GlobalProperty getGlobalProperty(String globalPropertyUuid) {
        GlobalProperty globalProperty = Context.getAdministrationService().getGlobalPropertyByUuid(globalPropertyUuid);
        if (globalProperty == null) {
            throw new CflRuntimeException(String.format("Unable to retrieve global property by uuid: %s",
                    globalPropertyUuid));
        }
        return globalProperty;
    }
}
