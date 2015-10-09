package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicRocketMQService;
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
import java.util.Set;

@Controller
@RequestMapping(value = "/api/topic")
public class TopicServiceController {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CockpitTopicRocketMQService cockpitTopicRocketMQService;

    @Autowired
    private CockpitBrokerService cockpitBrokerService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN) ? 0 : cockpitUser.getTeam().getId();
        List<Topic> topics = topicMapper.list(0, 0, null, null);
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
        Topic topicEntity = topicMapper.get(0, topic);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", 1);
        result.put("iTotalDisplayRecords", 1);
        result.put("aaData", topicEntity);
        return result;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public Topic lookUp(@PathVariable("topic") String topic) {
        return topicMapper.get(0, topic);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Topic add(@RequestBody Topic topic, long projectId, HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
//        if (null == topic.getBrokerAddress() || topic.getBrokerAddress().isEmpty()) {
//            DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
//            defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
//            try {
//                defaultMQAdminExt.start();
//                Set<String> brokers = cockpitBrokerService.getALLBrokers(defaultMQAdminExt);
//                for (String broker:brokers){
//                    // topic.setBrokerAddress(broker);
//                    // TODO Fix me.
//                    topicMapper.connectProject(topic.getId(), projectId);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }finally {
//                defaultMQAdminExt.shutdown();
//            }
//        } else {
//            topicMapper.connectProject(topic.getId(), projectId);
//        }
//        return topic;
        throw new RuntimeException("Not implemented.");
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody Topic topic) {
        topic.setUpdateTime(new Date());
        topicMapper.update(topic);
    }
}
