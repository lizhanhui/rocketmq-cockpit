package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by robert on 2015/11/5.
 */

@Controller
@RequestMapping(value = "/api/broker")
public class CockpitBrokerController {

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Broker> getBrokers() throws CockpitException{
        return cockpitBrokerDBService.list(null, null, 0, 0);
    }

}
