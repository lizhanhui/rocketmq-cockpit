package com.ndpmedia.rocketmq.cockpit.exception;


public class CockpitDataAccessException extends Exception {

    public CockpitDataAccessException() {
        super();
    }

    public CockpitDataAccessException(String message) {
        super(message);
    }

    public CockpitDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public CockpitDataAccessException(Throwable cause) {
        super(cause);
    }
}
