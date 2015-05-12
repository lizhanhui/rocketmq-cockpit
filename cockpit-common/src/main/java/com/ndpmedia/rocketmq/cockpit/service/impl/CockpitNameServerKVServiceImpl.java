package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.KV;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.NameServerKVMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitNameServerKVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("cockpitNameServerKVService")
public class CockpitNameServerKVServiceImpl implements CockpitNameServerKVService {

    @Autowired
    private NameServerKVMapper nameServerKVMapper;

    @Override
    public long add(final KV kv) {
        return nameServerKVMapper.insert(kv);
    }

    @Override
    public void delete(KV kv) {
        nameServerKVMapper.delete(kv.getId());
    }

    @Override
    public void delete(long id) {
        nameServerKVMapper.delete(id);
    }

    @Override
    public void update(KV kv) {
        nameServerKVMapper.update(kv);
    }

    @Override
    public KV get(long id) {
        return nameServerKVMapper.get(id);
    }

    @Override
    public List<KV> list() {
        return nameServerKVMapper.list();
    }

    @Override
    public List<KV> list(Status... statuses) {
        if (null == statuses) {
            return list();
        } else {
            int[] statusArray = new int[statuses.length];
            int i = 0;
            for (Status status : statuses) {
                statusArray[i++] = status.getId();
            }

            List<KV> kvList = nameServerKVMapper.listByStatus(statusArray);
            Collections.sort(kvList);
            return kvList;
        }
    }
}
