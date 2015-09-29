package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.model.TopicPerSecond;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.util.ConsumeProgressHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "/api/topic-progress")
public class TopicProgressServiceController {

    private Logger logger = LoggerFactory.getLogger(TopicProgressServiceController.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(HttpServletRequest request) {
        return consumeProgressMapper.topicList(null);
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public List<TopicPerSecond> list(@PathVariable("topic") String topic) {
        List<ConsumeProgress> consumeProgresses = consumeProgressMapper.brokerTPSList(null, topic, null, -1);
        Collections.reverse(consumeProgresses);
        return ConsumeProgressHelper.getTPSListFromDiffList(consumeProgresses);
    }

    @RequestMapping(value = "/d", method = RequestMethod.GET)
    @ResponseBody
    public List<String> simple() {
        List<String> result = new ArrayList<String>();
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            Set<String> bT = defaultMQAdminExt.fetchAllTopicList().getTopicList();
            List<String> dT = consumeProgressMapper.topicList(null);
            for (String temp : bT) {
                if (!dT.contains(temp))
                    result.add(temp);
            }
        } catch (MQClientException e) {
            logger.warn("[TopicProgressServiceController]try to get topic list failed." + e);
        } catch (InterruptedException e) {
            logger.warn("[TopicProgressServiceController]try to get topic list failed." + e);
        } catch (RemotingException e) {
            logger.warn("[TopicProgressServiceController]try to get topic list failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }

        return result;
    }
}
