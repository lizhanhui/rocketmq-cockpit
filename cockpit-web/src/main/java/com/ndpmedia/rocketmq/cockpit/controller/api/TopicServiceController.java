package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import com.ndpmedia.rocketmq.cockpit.util.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/topic")
public class TopicServiceController {

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN) ? 0 : cockpitUser.getTeam().getId();
        List<TopicMetadata> topics = cockpitTopicDBService.getTopics(null);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", topics.size());
        result.put("iTotalDisplayRecords", topics.size());
        result.put("aaData", topics);
        return result;
    }

    @RequestMapping(value = "/detail/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> detailList(HttpServletRequest request, @PathVariable("topic") String topic) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN) ? 0 : cockpitUser.getTeam().getId();
        TopicMetadata topicMetadata = cockpitTopicDBService.getTopic("DefaultCluster", topic);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", 1);
        result.put("iTotalDisplayRecords", 1);
        result.put("aaData", topicMetadata);
        return result;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public TopicMetadata lookUp(@PathVariable("topic") String topic) {
        return cockpitTopicDBService.getTopic("DefaultCluster", topic);
    }

    @RequestMapping(value = "/{projectId}",method = RequestMethod.PUT)
    @ResponseBody
    public TopicMetadata add(@RequestBody TopicMetadata topicMetadata, @PathVariable("projectId") long projectId, HttpServletRequest request) {
        cockpitTopicDBService.insert(topicMetadata, projectId);
        return topicMetadata;
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody TopicMetadata topic) {
        topic.setUpdateTime(new Date());
        cockpitTopicDBService.update(topic);
    }
}
