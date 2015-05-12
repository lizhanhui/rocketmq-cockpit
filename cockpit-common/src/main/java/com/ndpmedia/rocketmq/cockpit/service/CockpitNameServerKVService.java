package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.KV;
import com.ndpmedia.rocketmq.cockpit.model.Status;

import java.util.List;

public interface CockpitNameServerKVService {

    long add(KV kv);

    void delete(KV kv);

    void delete(long id);

    void update(KV kv);

    KV get(long id);

    List<KV> list();

    List<KV> list(Status... statuses);
}
