package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.IPPair;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.IpPairMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/ip")
public class IpMappingServiceController {

    @Autowired
    private IpPairMapper ipPairMapper;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public IPPair add(@RequestBody IPPair ipPair) {
        ipPairMapper.insert(ipPair);
        return ipPair;
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") long id) {
        ipPairMapper.delete(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@ModelAttribute IPPair ipPair) {
        ipPairMapper.update(ipPair);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<IPPair> list() {
        return ipPairMapper.list();
    }
}
