package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.common.protocol.body.Connection;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
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
@RequestMapping(value = "/api/consumer-group")
public class ConsumerGroupServiceController {

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request) {
        List<ConsumerGroup> consumerGroups = consumerGroupMapper.list(getProjectId(request), null, null, 0, null);
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
        return consumerGroupMapper.get(id, null);
    }

    @RequestMapping(value = "/cluster-name/{clusterName}", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumerGroup> listByClusterName(@PathVariable("clusterName") String clusterName,
                                                 HttpServletRequest request) {
        return consumerGroupMapper.list(getProjectId(request), clusterName, null, 0, null);
    }

    @RequestMapping(value = "/consumer-group-name/{consumerGroupName}", method = RequestMethod.GET)
    @ResponseBody
    public ConsumerGroup getByConsumerGroupName(@PathVariable("consumerGroupName") String consumerGroupName,
                                                HttpServletRequest request) {
        List<ConsumerGroup> groups = consumerGroupMapper.list(getProjectId(request), null, consumerGroupName, 0, null);
        if (groups.isEmpty())
            return new ConsumerGroup();
        return groups.get(0);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ConsumerGroup add(@RequestBody ConsumerGroup consumerGroup, long projectId) {
        cockpitConsumerGroupDBService.insert(consumerGroup, projectId);
        return consumerGroup;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody ConsumerGroup consumerGroup) {
        consumerGroup.setUpdateTime(new Date());
        consumerGroupMapper.update(consumerGroup);
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
            e.printStackTrace();
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }

    private long getTeamId(HttpServletRequest request) {
        long teamId = 0;
        if (!WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN)) {
            CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
            teamId = cockpitUser.getTeam().getId();
        }
        return teamId;
    }

    private long getProjectId(HttpServletRequest request) {
        throw new RuntimeException("Not initialized.");
    }

}
