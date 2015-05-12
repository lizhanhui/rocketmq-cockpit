package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import com.ndpmedia.rocketmq.cockpit.util.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/consume-progress")
public class ConsumeProgressServiceController {

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(HttpServletRequest request) {
        return consumeProgressMapper.consumerGroupList();
    }

    @RequestMapping(value = "/{consumerGroup}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(@PathVariable("consumerGroup") String consumerGroup, HttpServletRequest request) {
        return consumeProgressMapper.topicList(consumerGroup);
    }

    @RequestMapping(value = "/{consumerGroup}/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(@PathVariable("consumerGroup") String consumerGroup, @PathVariable("topic") String topic,
            HttpServletRequest request) {
        return consumeProgressMapper.brokerList(consumerGroup, topic);
    }

    @RequestMapping(value = "/{consumerGroup}/{topic}/{broker}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(@PathVariable("consumerGroup") String consumerGroup, @PathVariable("topic") String topic,
            @PathVariable("broker") String broker, HttpServletRequest request) {
        return consumeProgressMapper.queueList(consumerGroup, topic, broker);
    }

    @RequestMapping(value = "/{consumerGroup}/{topic}/{broker}/{queue}", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumeProgress> list(@PathVariable("consumerGroup") String consumerGroup,
            @PathVariable("topic") String topic, @PathVariable("broker") String broker,
            @PathVariable("queue") String queue, HttpServletRequest request) {
        if ("-1".equals(topic))
            return consumeProgressMapper.diffList(consumerGroup, null, null, -1);
        if ("-1".equals(broker))
            return consumeProgressMapper.diffList(consumerGroup, topic, null, -1);
        if ("-1".equals(queue))
            return consumeProgressMapper.diffList(consumerGroup, topic, broker, -1);

        return consumeProgressMapper.diffList(consumerGroup, topic, broker, Integer.parseInt(queue));
    }

    private long getTeamId(HttpServletRequest request) {
        long teamId = 0;
        if (!WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN)) {
            CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
            teamId = cockpitUser.getTeam().getId();
        }
        return teamId;
    }

}
