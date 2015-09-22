package com.ndpmedia.rocketmq.cockpit.util;

import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.model.TopicPerSecond;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 2015/9/22.
 */
public class ConsumeProgressHelper {

    public static List<TopicPerSecond> getTPSListFromDiffList(List<ConsumeProgress> consumeProgresses) {
        List<TopicPerSecond> topicPerSeconds = new ArrayList<>();
        long lastTime = 0L;
        long lastDiff = -1L;
        for (ConsumeProgress consumeProgress : consumeProgresses) {
            if (-1 != lastDiff) {
                TopicPerSecond topicPerSecond = new TopicPerSecond();
                topicPerSecond.setTimeStamp(consumeProgress.getCreateTime().getTime());
                if (consumeProgress.getBrokerOffset() >= lastDiff)
                    topicPerSecond.setTps(Math.round(100.0 * 1000 * (consumeProgress.getBrokerOffset() - lastDiff) /
                            (consumeProgress.getCreateTime().getTime() - lastTime))/100.0);
                else
                    topicPerSecond.setTps(0.00);
                topicPerSeconds.add(topicPerSecond);
            }
            lastTime = consumeProgress.getCreateTime().getTime();
            lastDiff = consumeProgress.getBrokerOffset();
        }
        return topicPerSeconds;
    }
}
