package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;

import java.util.List;
import java.util.Map;

/**
 * The AdHocMessagePatientFilterConverter Class.
 * <p>
 * The converters are responsible for converting client data provided by in
 * {@link AdHocMessagePatientFilterDTO} to a collection of {@link Condition}s.
 * </p>
 * <p>
 * The concrete instances are configured to be used at Global Parameter:
 * {@link org.openmrs.module.cfl.CFLConstants#AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY}.
 * </p>
 */
public interface PatientFilterConverter {
    /**
     * @param properties the configuration properties, not null
     * @throws IllegalArgumentException if the {@code properties} are missing required values
     */
    void initFilter(Map<String, Object> properties);

    /**
     * @param dto the DTO to convert to Conditions, not null
     * @return the list of Conditions which represent the filtering defined by {@code dto}
     * @throws IllegalStateException if this converter object was not initialed
     */
    List<Condition<?, ?>> convert(AdHocMessagePatientFilterDTO dto);
}
