package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.NameServer;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.NameServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/api/name-server")
public class NameServerServiceController {

    @Autowired
    private NameServerMapper nameServerMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<NameServer> list() {
        return nameServerMapper.list();
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public NameServer get(@PathVariable("id") long id) {
        return nameServerMapper.get(id);
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("id") long id) {
        nameServerMapper.delete(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public NameServer add(@RequestBody NameServer nameServer) {
        nameServer.setCreateTime(new Date());
        nameServer.setUpdateTime(new Date());
        nameServerMapper.insert(nameServer);
        return nameServer;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public NameServer update(@ModelAttribute NameServer nameServer) {
        long id = nameServerMapper.insert(nameServer);
        nameServer.setId(id);
        return nameServer;
    }
}
