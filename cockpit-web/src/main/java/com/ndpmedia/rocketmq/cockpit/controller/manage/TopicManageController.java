package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/manage/topic")
public class TopicManageController {

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public boolean update(@RequestBody TopicMetadata topicMetadata) throws CockpitException {
        return cockpitTopicMQService.createOrUpdateTopic(null, topicMetadata);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@RequestBody TopicMetadata topicMetadata) throws CockpitException {
        return cockpitTopicMQService.deleteTopic(null, topicMetadata);
    }

    @RequestMapping(value = "/{brokerId}/{topicId}",method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("brokerId") long brokerId, @PathVariable("topicId") long topicId) throws CockpitException {
        List<TopicBrokerInfo> infoList = cockpitTopicDBService.queryTopicBrokerInfoByTopic(topicId, brokerId, 0);
        if (infoList.size() > 0) {
            TopicBrokerInfo topicBrokerInfo = infoList.get(0);
            return cockpitTopicMQService.deleteTopicByBroker(null, topicBrokerInfo);
        }

        return true;
    }
}
