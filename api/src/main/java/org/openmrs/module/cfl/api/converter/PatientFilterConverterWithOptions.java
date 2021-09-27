package org.openmrs.module.cfl.api.converter;

import java.util.List;

public interface PatientFilterConverterWithOptions {
    String OPTIONS_PROP = "options";

    List<String> getOptions();
}
