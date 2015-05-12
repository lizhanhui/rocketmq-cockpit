package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.ndpmedia.rocketmq.cockpit.model.KV;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.service.CockpitNameServerKVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/name-server-kv")
public class NameServerKVServiceController {

    @Autowired
    private CockpitNameServerKVService cockpitNameServerKVService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public KV add(@RequestBody KV kv) {
        cockpitNameServerKVService.add(kv);
        return kv;
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public KV view(@PathVariable("id") long id) {
        return cockpitNameServerKVService.get(id);
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public KV delete(@PathVariable("id") long id) {
        KV kv = cockpitNameServerKVService.get(id);
        if (null != kv) {
            cockpitNameServerKVService.delete(kv);
            kv.setStatus(Status.DELETED);
        }
        return kv;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<KV> list() {
        return cockpitNameServerKVService.list();
    }

    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    public List<KV> list(@PathVariable(value = "status") String status) {
        return cockpitNameServerKVService.list(Status.valueOf(status));
    }
}
