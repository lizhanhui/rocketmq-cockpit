package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/api/project")
public class CockpitProjectController {

    @Autowired
    private CockpitProjectService cockpitProjectService;

    @Autowired
    private CockpitConsumerGroupService cockpitConsumerGroupService;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Project> list(HttpServletRequest request){
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        long teamId = cockpitUser.getTeam().getId();
        return cockpitProjectService.list(teamId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public boolean add(@RequestBody Project project, HttpServletRequest request){
        try {
            CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
            long teamId = cockpitUser.getTeam().getId();
            project.setTeamId(teamId);
            cockpitProjectService.insert(project);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/{project}/{consumerGroup}/{topic}", method = RequestMethod.PUT)
    @ResponseBody
    public void addRef(@PathVariable("project") String project, @PathVariable("consumerGroup") String consumerGroup, @PathVariable("topic") String topic){
        cockpitProjectService.addRef(project, consumerGroup, topic);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@RequestBody int id){
        cockpitProjectService.remove(id);
        return true;
    }

    @RequestMapping(value = "/{project}", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumerGroup> getConsumerGroups(@PathVariable("project") String project){
        List<ConsumerGroup> results = new ArrayList<>();
        List<String> groupNames = cockpitProjectService.getConsumerGroups(project);
        for (String groupName:groupNames){
            results.add(cockpitConsumerGroupService.getBaseBean(groupName));
        }
        return results;
    }

    @RequestMapping(value = "/{project}", method = RequestMethod.POST)
    @ResponseBody
    public List<Topic> getTopics(@PathVariable("project") String project){
        List<Topic> results = new ArrayList<>();
        List<String> topicNames = cockpitProjectService.getTopics(project);
        for (String topicName:topicNames){
            results.add(cockpitTopicService.getBaseBean(topicName));
        }
        return results;
    }
}
