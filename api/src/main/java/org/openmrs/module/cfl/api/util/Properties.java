package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;

import java.util.Map;

public class Properties {

    private final Map<String, Object> propertiesMap;

    public Properties(Map<String, Object> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    public Object get(String propertyName) {
        return propertiesMap.get(propertyName);
    }

    public Properties getNestedProperties(String propertyName) {
        Object value = propertiesMap.get(propertyName);
        if (value == null) {
            return null;
        } else if (value instanceof Map<?, ?>) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Object key : map.keySet()) {
                if (!(key instanceof String)) {
                    throw new CflRuntimeException(String.format(
                            "Cannot parse %s as nested properties - not all keys are strings: %s",
                            propertyName,
                            value));
                }
            }
            return new Properties((Map<String, Object>) value);
        } else {
            throw new CflRuntimeException(String.format(
                    "Cannot parse %s as nested properties - invalid type: %s ",
                    propertyName,
                    value.getClass().getName()));
        }
    }

    public String getString(String propertyName) {
        Object value = propertiesMap.get(propertyName);
        return value == null ? null : value.toString();
    }

    public Integer getInt(String propertyName) {
        Object value = propertiesMap.get(propertyName);
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt(value.toString());
        } else {
            throw new CflRuntimeException(String.format("Cannot parse %s as int - invalid type: %s ",
                    propertyName,
                    value.getClass().getName()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "Properties" + propertiesMap;
    }
}
