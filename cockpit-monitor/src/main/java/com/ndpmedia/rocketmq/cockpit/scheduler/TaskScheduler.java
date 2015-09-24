package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumeProgressNSService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupNSService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * consumer group base offset scheduler
 */
@Component
public class TaskScheduler {

    private Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private CockpitConsumeProgressNSService cockpitConsumeProgressNSService;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @Autowired
    private CockpitConsumerGroupNSService cockpitConsumerGroupNSService;

    private static AtomicInteger counts = new AtomicInteger(0);

    private static ConcurrentMap<String, Integer> groupTableRel = new ConcurrentHashMap();

    private static Date date = new Date();

    private void init(){
        try {
            counts.set(this.consumeProgressMapper.groupTableMAXID());
        }catch (Exception e){
            //nothing todo;
        }

            List<Map<Object, Object>> xrefs = this.consumeProgressMapper.groupTableXREFList();
            for (Map<Object, Object> xref : xrefs){
                try{
                    groupTableRel.put(xref.get("group_name").toString(), Integer.parseInt(xref.get("id").toString()));
                }finally {

                }
            }

    }
    /**
     * schedule:get consumer group and the topic offset.
     * period:300 second
     */
    @Scheduled(fixedRate = 300000)
    public void queryAccumulation() {
        if (groupTableRel.size() == 0 )
            init();
        date = new Date();

        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()) + "taskScheduler");
        defaultMQAdminExt.setAdminExtGroup(Long.toString(System.currentTimeMillis()) + "taskScheduler");
        try {
            defaultMQAdminExt.start();
            Set<String> groupList = cockpitConsumerGroupNSService.getGroups(defaultMQAdminExt);

            if (groupList.size() > groupTableRel.size()){
                createPrivateTable(groupList);
            }

            List<ConsumeProgress> consumeProgressList;
            for (String group : groupList) {
                consumeProgressList = cockpitConsumeProgressNSService.queryConsumerProgress(defaultMQAdminExt, group, null, null);
                for (ConsumeProgress cp : consumeProgressList) {
                    if (null == cp || null == cp.getTopic() || null == cp.getBrokerName()) {
                        continue;
                    }

                    updateConsumeProgressData(cp);
                }
            }

            updateTopicProgressData();
        } catch (Exception e) {
            if (!e.getMessage().contains("offset table is empty")) {
                logger.warn("[MONITOR][CONSUME-PROGRESS] main method failed." + e);
            }
        } finally {
            defaultMQAdminExt.shutdown();
        }
    }

    private void createPrivateTable(Set<String> groups){
        System.out.println("[MONITOR][CONSUME-PROGRESS] GROUP COUNTS HAS CHANGE. TRY TO UPDATE TABLES. TOTAL GROUP: " + groups.size());
        for (String group:groups){
            if (!groupTableRel.containsKey(group)) {
                consumeProgressMapper.create(String.valueOf(counts.addAndGet(1)));
                groupTableRel.put(group, counts.get());
                consumeProgressMapper.groupTableXREFInsert(groupTableRel.get(group), group);
            }
        }
        System.out.println("[MONITOR][CONSUME-PROGRESS] UPDATE GROUP TABLES ALREADY DONE. TOTAL COUNTS: " + groupTableRel.size());
    }

    private void updateConsumeProgressData(ConsumeProgress consumeProgress){
        consumeProgress.setCreateTime(date);
        consumeProgress.setTableID(groupTableRel.get(consumeProgress.getConsumerGroup()));
        consumeProgressMapper.insert(consumeProgress);
        consumeProgressMapper.insertPrivate(consumeProgress);
    }

    private void updateTopicProgressData(){
        consumeProgressMapper.updateTopicProgress(date);
    }
}
