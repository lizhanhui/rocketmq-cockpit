package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/manage/user")
public class UserManageController {
    @Autowired
    private CockpitUserMapper cockpitUserMapper;

    @RequestMapping(value = "/{status}", method = RequestMethod.GET)
    @ResponseBody
    public List<CockpitUser> list(@PathVariable("status") String status) {
        if ("active".equalsIgnoreCase(status)) {
            return cockpitUserMapper.list(Status.ACTIVE);
        } else if ("draft".equalsIgnoreCase(status)) {
            return cockpitUserMapper.list(Status.DRAFT);
        } else if ("deleted".equalsIgnoreCase(status)) {
            return cockpitUserMapper.list(Status.DELETED);
        } else {
            return cockpitUserMapper.list(null);
        }
    }


    @RequestMapping(value = "/activate/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CockpitUser activate(@PathVariable("id") long id) {
        cockpitUserMapper.activate(id);
        return cockpitUserMapper.get(id, null);
    }

    @RequestMapping(value = "/suspend/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CockpitUser suspend(@PathVariable("id") long id) {
        cockpitUserMapper.suspend(id);
        return cockpitUserMapper.get(id, null);
    }
}
