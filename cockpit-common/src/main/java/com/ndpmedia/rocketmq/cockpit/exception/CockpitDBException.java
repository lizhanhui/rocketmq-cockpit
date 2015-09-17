package com.ndpmedia.rocketmq.cockpit.exception;


public class CockpitDBException extends Exception {

    public CockpitDBException() {
        super();
    }

    public CockpitDBException(String message) {
        super(message);
    }

    public CockpitDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public CockpitDBException(Throwable cause) {
        super(cause);
    }
}
