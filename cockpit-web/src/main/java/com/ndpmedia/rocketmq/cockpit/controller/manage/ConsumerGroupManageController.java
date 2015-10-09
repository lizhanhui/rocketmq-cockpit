package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/manage/consumer-group")
public class ConsumerGroupManageController {

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public boolean update(@ModelAttribute ConsumerGroup consumerGroup) {
        return cockpitConsumerGroupMQService.update(consumerGroup);
    }

    @RequestMapping(value = "/{clusterName}/{groupName}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("clusterName") String clusterName, @PathVariable("groupName") String groupName){
        ConsumerGroup consumerGroup = new ConsumerGroup();
        consumerGroup.setClusterName(clusterName);
        consumerGroup.setGroupName(groupName);
        return cockpitConsumerGroupMQService.clear(consumerGroup);
    }

}
