package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
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
    private CockpitTopicService cockpitTopicService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public boolean update(@RequestBody Topic topic) {
        return cockpitTopicService.createOrUpdateTopic(topic);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@RequestBody Topic topic) {
        return cockpitTopicService.deleteTopic(topic);
    }

}
