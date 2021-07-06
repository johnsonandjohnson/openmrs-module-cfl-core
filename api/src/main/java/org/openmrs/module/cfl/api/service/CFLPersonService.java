package org.openmrs.module.cfl.api.service;

import org.openmrs.module.cfl.api.contract.CFLPerson;

import java.util.List;

public interface CFLPersonService {

    List<CFLPerson> findByPhone(String phone, boolean dead);

    void savePersonAttribute(Integer personId, String attributeTypeName, String attributeValue);
}
