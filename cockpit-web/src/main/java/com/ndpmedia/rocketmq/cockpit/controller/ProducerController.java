package com.ndpmedia.rocketmq.cockpit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * try to open cockpit producer index.jsp.
 * it have:
 * 1 find producer by group name
 */

@Controller
@RequestMapping(value = "/producer")
public class ProducerController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        return "producer/index";
    }
}
