package com.ndpmedia.rocketmq.cockpit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public final class Helper {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static String getInstanceName() {
        String localHostAddress;
        try {
            localHostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localHostAddress = "127.0.0.1";
        }

        return localHostAddress + "@" + Thread.currentThread().getId() + "_" + COUNTER.incrementAndGet();
    }


}
