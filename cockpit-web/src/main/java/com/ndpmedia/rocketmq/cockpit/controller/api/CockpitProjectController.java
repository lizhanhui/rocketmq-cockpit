package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitRuntimeException;
import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.ResourceType;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TeamMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
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
    private TopicMapper topicMapper;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private TeamMapper teamMapper;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Project> list(HttpServletRequest request){
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        if (isAdmin(cockpitUser)) {
            // return all projects.
            return cockpitProjectService.list(0);
        }

        long teamId = cockpitUser.getTeam().getId();
        return cockpitProjectService.list(teamId);
    }

    private boolean isAdmin(CockpitUser user) {
        List<CockpitRole> roles = user.getCockpitRoles();

        for (CockpitRole role : roles) {
            if (CockpitRole.ROLE_ADMIN.equals(role)) {
                return true;
            }
        }

        return false;
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

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public Project get(@PathVariable("projectId") long projectId, HttpServletRequest request) {
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);

        if (isAdmin(cockpitUser)) {
            return cockpitProjectService.get(projectId);
        }

        if (teamMapper.hasAccess(cockpitUser.getTeam().getId(), projectId, ResourceType.PROJECT)) {
            return cockpitProjectService.get(projectId);
        } else {
            throw new CockpitRuntimeException("Access Denied");
        }
    }

    @RequestMapping(value = "/{projectId}/{consumerGroupId}/{topicId}", method = RequestMethod.PUT)
    @ResponseBody
    public void addProjectResources(@PathVariable("projectId") long projectId,
                                    @PathVariable("consumerGroupId") long consumerGroupId,
                                    @PathVariable("topicId") long topicId){
        topicMapper.connectProject(topicId, projectId);
        consumerGroupMapper.connectProject(consumerGroupId, projectId);
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

    @RequestMapping(value = "/{projectId}/topics", method = RequestMethod.POST)
    @ResponseBody
    public List<TopicMetadata> getTopics(@PathVariable("projectId") long projectId){
        return cockpitProjectService.getTopics(projectId);
    }
}
