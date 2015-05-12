package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
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
    private TopicMapper topicMapper;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN) ? 0 : cockpitUser.getTeam().getId();
        List<Topic> topics = topicMapper.list(teamId, null);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", topics.size());
        result.put("iTotalDisplayRecords", topics.size());
        result.put("aaData", topics);
        return result;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public List<Topic> lookUp(@PathVariable("topic") String topic) {
        return topicMapper.list(0, topic);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Topic add(@RequestBody Topic topic, HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        cockpitTopicService.insert(topic, cockpitUser.getTeam().getId());
        return topic;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") long id, HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        topicMapper.delete(id);
        cockpitTopicService.remove(id, cockpitUser.getTeam().getId());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void register(@PathVariable("id") long id) {
        topicMapper.register(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody Topic topic) {
        topic.setUpdateTime(new Date());
        topicMapper.update(topic);
    }
}
