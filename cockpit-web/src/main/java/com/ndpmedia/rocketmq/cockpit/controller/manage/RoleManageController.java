package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/manage/role")
public class RoleManageController {

    @Autowired
    private CockpitRoleMapper cockpitRoleMapper;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CockpitRole> list() {
        return cockpitRoleMapper.list();
    }

}
