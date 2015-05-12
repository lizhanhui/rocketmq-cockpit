package com.ndpmedia.rocketmq.cockpit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    public String showHome() {
        return "forward:/cockpit/home";
    }


    @RequestMapping(value = "ui", method = RequestMethod.GET)
    public String ui() {
        return "ui-demo";
    }


}
