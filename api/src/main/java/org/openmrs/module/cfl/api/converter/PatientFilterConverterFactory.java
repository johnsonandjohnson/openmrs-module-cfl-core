/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PatientFilterConverterFactory {
  private static final Map<String, Supplier<PatientFilterConverter>> CONVERTERS = new HashMap<>();

  static {
    registerConverter(
        "PatientFilterAddressFieldStringConverter", PatientFilterAddressFieldStringConverter::new);
    registerConverter("PatientFilterAgeToDateConverter", PatientFilterAgeToDateConverter::new);
    registerConverter(
        "PatientFilterAttributeStringConverter", PatientFilterAttributeStringConverter::new);
    registerConverter(
        "PatientFilterEntityFieldStringConverter", PatientFilterEntityFieldStringConverter::new);
    registerConverter(
        "PatientFilterReceivedDosagesConverter", PatientFilterReceivedDosagesConverter::new);
    registerConverter("PatientFilterStringListConverter", PatientFilterStringListConverter::new);
  }

  private PatientFilterConverterFactory() {}

  public static void registerConverter(
      String name, Supplier<PatientFilterConverter> patientFilterConverterSupplier) {
    CONVERTERS.put(name, patientFilterConverterSupplier);
  }

  public static PatientFilterConverter getPatientFilterConverter(String name) {
    final Supplier<PatientFilterConverter> patientFilterConverterSupplier = CONVERTERS.get(name);

    if (patientFilterConverterSupplier == null) {
      throw new IllegalStateException(
          "Unknown PatientFilterConverter for name: "
              + name
              + ". Registered converters: "
              + CONVERTERS.keySet());
    }

    return patientFilterConverterSupplier.get();
  }
}
