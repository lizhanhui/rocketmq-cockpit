package com.ndpmedia.rocketmq.cockpit.scheduler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                        "classpath*:applicationContextCommon.xml",
                        "classpath*:applicationContextMonitor.xml");
    }
}
