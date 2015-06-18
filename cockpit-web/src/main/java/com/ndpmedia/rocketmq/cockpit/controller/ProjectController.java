package com.ndpmedia.rocketmq.cockpit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/project")
public class ProjectController {

    @RequestMapping(value = "/create")
    public String showCreate() {
        return "project/create-new-project";
    }

    @RequestMapping(value = "/manage")
    public String showManage() {
        return "project/manage-projects";
    }

    @RequestMapping(value = "/manage2")
    public String showManage2() {
        return "project/manage-projects2";
    }

}
