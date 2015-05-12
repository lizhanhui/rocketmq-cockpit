package com.ndpmedia.rocketmq.cockpit.controller;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.KV;
import com.ndpmedia.rocketmq.cockpit.service.CockpitNameServerKVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/name-server")
public class NameServerController {

    @Autowired
    private CockpitNameServerKVService cockpitNameServerKVService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        return "name-server/index";
    }


    @RequestMapping(value = "/kv", method = RequestMethod.GET)
    public ModelAndView listKV() {
        ModelAndView modelAndView = new ModelAndView("name-server/kv/list");
        modelAndView.addObject("list", cockpitNameServerKVService.list());
        return modelAndView;
    }

    @RequestMapping(value = "/kv/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void apply(@PathVariable("id") long id)
            throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        KV kv = cockpitNameServerKVService.get(id);
        if (null != kv) {
            DefaultMQAdminExt mqAdmin = new DefaultMQAdminExt();
            mqAdmin.createAndUpdateKvConfig(kv.getNameSpace(), kv.getKey(), kv.getValue());
        }
    }
}
