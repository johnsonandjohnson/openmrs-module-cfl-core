package org.openmrs.module.cfl.web.monitor;

/**
 * The SystemStatusResponseBody Class represents a variant body of HTTP response of
 * {@link org.openmrs.module.cfl.web.controller.MonitoringController#getSystemStatus(String, String)} method.
 * <p>
 * Use {@link SystemStatusResponseBodyBuilder}.
 * </p>
 */
public class SystemStatusResponseBodyStatusOnly implements SystemStatusResponseBody {
    private final String status;

    SystemStatusResponseBodyStatusOnly(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
