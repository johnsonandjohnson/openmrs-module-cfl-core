package org.openmrs.module.cfl.api.exception;

public class CflRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -1434211965543042192L;

    public CflRuntimeException(String message) {
        super(message);
    }

    public CflRuntimeException(String message, Throwable exception) {
        super(message, exception);
    }

    public CflRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
