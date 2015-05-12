package com.ndpmedia.rocketmq.cockpit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * try to open cockpit consumer index.jsp
 * it have
 * 1 find consumers by group name
 */

@Controller
@RequestMapping(value = "/consumer-group")
public class ConsumerGroupController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        return "consumer-group/index";
    }
}
