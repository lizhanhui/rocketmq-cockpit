package com.ndpmedia.rocketmq.cockpit.util;

import com.ndpmedia.rocketmq.cockpit.model.Level;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Warning;

import java.util.Date;

/**
 * Created by robert on 2016/1/11.
 */
public class WarnMsgHelper {

    public static Warning makeWarning(Level level, String msg) {
        // Add a warning
        Warning warning = new Warning();
        warning.setLevel(level);
        warning.setStatus(Status.ACTIVE);
        warning.setCreateTime(new Date());
        warning.setMsg(msg);
        return warning;
    }
}
