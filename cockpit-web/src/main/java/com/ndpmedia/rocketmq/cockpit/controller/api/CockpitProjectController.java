package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitRuntimeException;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.ResourceType;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitUserService;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import com.ndpmedia.rocketmq.cockpit.util.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/project")
public class CockpitProjectController {

    @Autowired
    private CockpitProjectService cockpitProjectService;

    @Autowired
    private CockpitUserService cockpitUserService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Project> list(HttpServletRequest request){
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        if (UserHelper.isAdmin(cockpitUser)) {
            // return all projects.
            return cockpitProjectService.list(0);
        }

        long teamId = cockpitUser.getTeam().getId();
        return cockpitProjectService.list(teamId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public long add(@RequestBody Project project, HttpServletRequest request){
        try {
            CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
            long teamId = cockpitUser.getTeam().getId();
            project.setTeamId(teamId);
            cockpitProjectService.insert(project);

            return cockpitProjectService.get(-1, project.getName()).getId();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public Project get(@PathVariable("projectId") long projectId, HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);

        if (UserHelper.isAdmin(cockpitUser)) {
            return cockpitProjectService.get(projectId, null);
        }

        if (cockpitUserService.hasAccess(cockpitUser.getTeam().getId(), projectId, ResourceType.PROJECT)) {
            return cockpitProjectService.get(projectId, null);
        } else {
            throw new CockpitRuntimeException("Access Denied");
        }
    }

    @RequestMapping(value = "/{projectId}/{consumerGroupId}/{topicId}", method = RequestMethod.PUT)
    @ResponseBody
    public void addProjectResources(@PathVariable("projectId") long projectId,
                                    @PathVariable("consumerGroupId") long consumerGroupId,
                                    @PathVariable("topicId") long topicId){
        if (topicId > 0)
            cockpitProjectService.addTopic(projectId, topicId);
        if (consumerGroupId > 0)
            cockpitProjectService.addConsumerGroup(projectId, consumerGroupId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@RequestBody int id){
        cockpitProjectService.remove(id);
        return true;
    }

    @RequestMapping(value = "/{projectId}/consumer-groups", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumerGroup> getConsumerGroups(@PathVariable("projectId") long projectId){
        return cockpitProjectService.getConsumerGroups(projectId);
    }

    @RequestMapping(value = "/{projectId}/unconsumer-groups", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsumerGroup> getUnuseConsumerGroups(@PathVariable("projectId") long projectId){
        return cockpitProjectService.getUnuseConsumerGroups(projectId);
    }

    @RequestMapping(value = "/{projectId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public List<TopicMetadata> getTopics(@PathVariable("projectId") long projectId){
        return cockpitProjectService.getTopics(projectId);
    }

    @RequestMapping(value = "/{projectId}/untopics", method = RequestMethod.GET)
    @ResponseBody
    public List<TopicMetadata> getUnuseTopics(@PathVariable("projectId") long projectId){
        return cockpitProjectService.getUnuseTopics(projectId);
    }
}
