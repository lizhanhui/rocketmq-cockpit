package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/manage/topic")
public class TopicManageController {

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

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

}
