package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/api/topic-progress")
public class TopicProgressServiceController {

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> list(HttpServletRequest request) {
        return consumeProgressMapper.topicList(null);
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumeProgress> list(@PathVariable("topic") String topic) {
        return consumeProgressMapper.brokerTPSList(null, topic, null, -1);
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
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
