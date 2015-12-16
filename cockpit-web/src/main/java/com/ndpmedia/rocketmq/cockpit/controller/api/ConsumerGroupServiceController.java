package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.common.protocol.body.Connection;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = "/api/consumer-group")
public class ConsumerGroupServiceController {

    private Logger logger = LoggerFactory.getLogger(ConsumerGroupServiceController.class);

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        //TODO get user project
        List<ConsumerGroup> consumerGroups = cockpitConsumerGroupDBService.list(0, null, null, 0, null);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sEcho", 1);
        result.put("iTotalRecords", consumerGroups.size());
        result.put("iTotalDisplayRecords", consumerGroups.size());
        result.put("aaData", consumerGroups);
        return result;
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ConsumerGroup get(@PathVariable("id") long id) {
        return cockpitConsumerGroupDBService.get(id, null);
    }

    @RequestMapping(value = "/cluster-name/{clusterName}", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumerGroup> listByClusterName(@PathVariable("clusterName") String clusterName,
                                                 HttpServletRequest request) {
        return cockpitConsumerGroupDBService.list(getProjectId(request), clusterName, null, 0, null);
    }

    @RequestMapping(value = "/consumer-group-name/{consumerGroupName}", method = RequestMethod.GET)
    @ResponseBody
    public ConsumerGroup getByConsumerGroupName(@PathVariable("consumerGroupName") String consumerGroupName,
                                                HttpServletRequest request) {
        //TODO user can not get every group
//        List<ConsumerGroup> groups = consumerGroupMapper.list(getProjectId(request), null, consumerGroupName, 0, null);
        return cockpitConsumerGroupDBService.get(-1, consumerGroupName);
//        if (groups.isEmpty())
//            return new ConsumerGroup();
//        return groups.get(0);
    }

    @RequestMapping(value = "/{projectId}",method = RequestMethod.PUT)
    @ResponseBody
    public ConsumerGroup add(@RequestBody ConsumerGroup consumerGroup, @PathVariable("projectId") long projectId) {
        cockpitConsumerGroupDBService.insert(consumerGroup, projectId);
        return consumerGroup;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody ConsumerGroup consumerGroup) {
        consumerGroup.setUpdateTime(new Date());
        cockpitConsumerGroupDBService.update(consumerGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") long id) {
        cockpitConsumerGroupDBService.delete(id);
    }

    @RequestMapping(value = "/client/{consumerGroup}", method = RequestMethod.GET)
    @ResponseBody
    public Set<Connection> getConsumerGroupClient(@PathVariable("consumerGroup") String consumerGroup){
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            return defaultMQAdminExt.examineConsumerConnectionInfo(consumerGroup).getConnectionSet();
        } catch (Exception e) {
            logger.warn("[ConsumerGroupServiceController]try to get consumer group client failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }

    private long getProjectId(HttpServletRequest request) {
        throw new RuntimeException("Not initialized.");
    }

}
