package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.Team;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/team")
public class TeamServiceController {

    @Autowired
    private TeamMapper teamMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Team> list() {
        return teamMapper.list();
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Team getById(@PathVariable("id") long id) {
        return teamMapper.get(id);
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Team create(@RequestBody Team team) {
        teamMapper.insert(team);
        return team;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Team update(@RequestBody Team team) {
        teamMapper.insert(team);
        return team;
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") long id) {
        teamMapper.delete(id);
    }
}
