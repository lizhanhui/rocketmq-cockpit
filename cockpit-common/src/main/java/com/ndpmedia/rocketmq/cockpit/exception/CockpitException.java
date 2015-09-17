package com.ndpmedia.rocketmq.cockpit.exception;


public class CockpitException extends Exception {

    public CockpitException() {
        super();
    }

    public CockpitException(String message) {
        super(message);
    }

    public CockpitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CockpitException(Throwable cause) {
        super(cause);
    }
}
