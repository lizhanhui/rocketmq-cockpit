package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.alibaba.rocketmq.common.MixAll;
import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/manage/consumerGroup")
public class ConsumerGroupManageController {

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @RequestMapping(value = "/{groupName}", method = RequestMethod.GET)
    @ResponseBody
    public boolean update(@PathVariable("groupName") String groupName) {
        ConsumerGroup consumerGroup = cockpitConsumerGroupDBService.get(0, groupName);
        return cockpitConsumerGroupMQService.update(consumerGroup);
    }

    @RequestMapping(value = "/{clusterName}/{groupName}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("clusterName") String clusterName, @PathVariable("groupName") String groupName){
        ConsumerGroup consumerGroup = new ConsumerGroup();
        consumerGroup.setClusterName(clusterName);
        consumerGroup.setGroupName(groupName);

        TopicMetadata topicMetadataR = new TopicMetadata();
        topicMetadataR.setTopic(MixAll.getRetryTopic(groupName));

        TopicMetadata topicMetadataD = new TopicMetadata();
        topicMetadataD.setTopic(MixAll.getDLQTopic(groupName));
        try {
            return cockpitConsumerGroupMQService.clear(consumerGroup)&&cockpitTopicMQService.deleteTopic(null , topicMetadataR)
                    &&cockpitTopicMQService.deleteTopic(null, topicMetadataD);
        } catch (CockpitException e) {
            System.out.println(e);
        }
        return false;
    }

}
