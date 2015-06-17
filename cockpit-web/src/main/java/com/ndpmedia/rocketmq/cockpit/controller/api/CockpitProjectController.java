package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@RequestBody int id){
        cockpitProjectService.remove(id);
        return true;
    }
}
