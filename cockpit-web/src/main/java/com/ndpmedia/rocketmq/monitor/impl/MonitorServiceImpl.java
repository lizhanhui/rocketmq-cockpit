package com.ndpmedia.rocketmq.monitor.impl;

import com.alibaba.fastjson.JSON;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Override
    public String monitor() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        List<Map<Object, Object>> result = consumeProgressMapper.lastDiff("%", null, null, -1, calendar.getTime());

        return JSON.toJSONString(getDiff(result));
    }

    @Override
    public List<ConsumeProgress> monitor(String consumerGroup) {
        return consumeProgressMapper.list(consumerGroup, null, null, -1);
    }

    @Override
    public List<ConsumeProgress> monitor(String consumerGroup, String topic) {
        return consumeProgressMapper.list(consumerGroup, topic, null, -1);
    }

    private List<List<Object>> getDiff(List<Map<Object, Object>>  consumeProgresses){
        List<List<Object>> result = new ArrayList<List<Object>>();
        for (Map<Object, Object> consumeProgress:consumeProgresses){
            List<Object> temp = new ArrayList<Object>();
            temp.add(consumeProgress.get("consumerGroup"));
            temp.add(consumeProgress.get("diff"));
            temp.add(null == consumeProgress.get("threshold") ? 1000 : consumeProgress.get("threshold"));
            result.add(temp);
        }

        return result;
    }
}
