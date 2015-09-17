package com.ndpmedia.rocketmq.cockpit.exception;


public class CockpitRuntimeException extends RuntimeException {

    public CockpitRuntimeException() {
        super();
    }

    public CockpitRuntimeException(String message) {
        super(message);
    }

    public CockpitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CockpitRuntimeException(Throwable cause) {
        super(cause);
    }
}
