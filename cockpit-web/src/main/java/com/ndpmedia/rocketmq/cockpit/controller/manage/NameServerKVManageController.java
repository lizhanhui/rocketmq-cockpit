package com.ndpmedia.rocketmq.cockpit.controller.manage;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.KV;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.service.CockpitNameServerKVService;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/manage/name-server-kv")
public class NameServerKVManageController {

    @Autowired
    private CockpitNameServerKVService cockpitNameServerKVService;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public KV apply(@PathVariable("id") long id) throws Exception {
        KV kv = cockpitNameServerKVService.get(id);
        if (null != kv) {
            DefaultMQAdminExt mqAdmin = new DefaultMQAdminExt();
            mqAdmin.setInstanceName(Helper.getInstanceName());
            mqAdmin.start();
            mqAdmin.createAndUpdateKvConfig(kv.getNameSpace(), kv.getKey(), kv.getValue());
            kv.setStatus(Status.ACTIVE);
            cockpitNameServerKVService.update(kv);
        }
        return kv;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("id") long id){
        DefaultMQAdminExt mqAdmin = new DefaultMQAdminExt();
        try {
            KV kv = cockpitNameServerKVService.get(id);
            if (null != kv) {
                mqAdmin.setInstanceName(Helper.getInstanceName());
                mqAdmin.start();
                mqAdmin.deleteKvConfig(kv.getNameSpace(), kv.getKey());
            }
        }catch (Exception e){
            return false;
        }
        finally {
            mqAdmin.shutdown();
        }
        return true;
    }

}
