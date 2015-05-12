package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/users")
public class CockpitUserServiceController {

    @Autowired
    private CockpitUserMapper cockpitUserMapper;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CockpitUser get(@PathVariable("id") long id) {
        return cockpitUserMapper.get(id, null);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<CockpitUser> list() {
        return cockpitUserMapper.list(null);
    }
}
