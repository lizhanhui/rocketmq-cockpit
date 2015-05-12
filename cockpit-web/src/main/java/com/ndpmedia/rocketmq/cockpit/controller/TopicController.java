package com.ndpmedia.rocketmq.cockpit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * try to open cockpit topic index.jsp.
 * it have:
 * 1 find topics
 */

@Controller
@RequestMapping(value = "/topic")
public class TopicController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        return "topic/index";
    }
}
