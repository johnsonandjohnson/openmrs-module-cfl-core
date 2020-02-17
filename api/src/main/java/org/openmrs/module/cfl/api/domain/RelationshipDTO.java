package org.openmrs.module.cfl.api.domain;

import java.io.Serializable;

public class RelationshipDTO implements Serializable {

    private static final long serialVersionUID = 4701954170273682390L;

    private String uuid;

    private String name;

    private String type;

    public String getUuid() {
        return uuid;
    }

    public RelationshipDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public RelationshipDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public RelationshipDTO setType(String type) {
        this.type = type;
        return this;
    }
}
