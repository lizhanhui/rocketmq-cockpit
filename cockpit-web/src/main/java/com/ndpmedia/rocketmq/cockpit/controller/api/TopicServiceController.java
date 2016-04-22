package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.*;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
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
import java.util.*;

@Controller
@RequestMapping(value = "/api/topic")
public class TopicServiceController {

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN) ? 0 : cockpitUser.getTeam().getId();
        List<TopicMetadata> topics = cockpitTopicDBService.getTopics((Status)null);
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
        List<TopicBrokerInfo> topicBrokerInfos = cockpitTopicDBService.queryTopicBrokerInfoByTopic(topicMetadata.getId(), 0L, 0);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", 1);
        result.put("iTotalDisplayRecords", 1);
        result.put("aaData", topicBrokerInfos);
        return result;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public TopicMetadata lookUp(@PathVariable("topic") String topic) {
        return cockpitTopicDBService.getTopic("DefaultCluster", topic);
    }

    @RequestMapping(value = "/{projectId}",method = RequestMethod.PUT)
    @ResponseBody
    public long add(@RequestBody TopicMetadata topicMetadata, @PathVariable("projectId") long projectId, HttpServletRequest request) {
        topicMetadata.setStatus(Status.DRAFT);
        cockpitTopicDBService.insert(topicMetadata, projectId);
        return topicMetadata.getId();
    }

    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseBody
    public boolean activate(@RequestBody TopicBrokerInfo topicBrokerInfo) throws CockpitException {
        try {
            TopicMetadata topicMetadata = cockpitTopicDBService.getTopic(topicBrokerInfo.getTopicMetadata().getClusterName(), topicBrokerInfo.getTopicMetadata().getTopic());

            cockpitTopicDBService.activate(topicMetadata.getId(), topicBrokerInfo.getBroker().getId());
        } catch (Exception e){
            throw new CockpitException("" + e);
        }
        return true;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public boolean add(@RequestBody TopicBrokerInfo topicBrokerInfo) throws CockpitException {
        try {
            TopicMetadata topicMetadata = cockpitTopicDBService.getTopic(topicBrokerInfo.getTopicMetadata().getClusterName(), topicBrokerInfo.getTopicMetadata().getTopic());

            topicBrokerInfo.setTopicMetadata(topicMetadata);
            topicBrokerInfo.setStatus(Status.ACTIVE);

            List<Broker> brokers = new ArrayList<>();
            if (topicBrokerInfo.getBroker().getId() == -1L){
                brokers.addAll(cockpitBrokerDBService.list(topicBrokerInfo.getTopicMetadata().getClusterName(), null, 0, 0));
            }else {
                brokers.add(cockpitBrokerDBService.get(0, topicBrokerInfo.getBroker().getAddress()));
            }

            for (Broker broker:brokers) {
                topicBrokerInfo.setBroker(broker);

                if (topicBrokerInfoCheck(topicBrokerInfo)) {
                    cockpitTopicDBService.insertTopicBrokerInfo(topicBrokerInfo);
                }else
                    cockpitTopicDBService.update(topicBrokerInfo);
            }
        }catch (Exception e){
            if (e.getMessage().contains("Error updating database") && e.getMessage().contains("Duplicate entry"))
                throw new CockpitException("this broker already added.");
            else
                throw new CockpitException(e);
        }
        return true;
    }

    @RequestMapping(value = "/{topicId}/{brokerId}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean deleteTopicBrokerInfo(@PathVariable("topicId") long topicId, @PathVariable("brokerId") long brokerId) {
        return cockpitTopicDBService.deactivate(topicId, brokerId);
    }

    @RequestMapping(value = "/{topicId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTopic(){

    }

    private boolean topicBrokerInfoCheck(TopicBrokerInfo topicBrokerInfo){
        List<TopicBrokerInfo> topicBrokerInfos = cockpitTopicDBService.queryTopicBrokerInfoByTopic(topicBrokerInfo.getTopicMetadata().getId(),
                topicBrokerInfo.getBroker().getId(), 0);

        if (null != topicBrokerInfos && topicBrokerInfos.size() > 0)
            return false;

        return true;
    }
}
