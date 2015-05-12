package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.NameServer;

import java.util.List;

public interface NameServerMapper {

    List<NameServer> list();

    long insert(NameServer nameServer);

    void update(NameServer nameServer);

    void delete(long id);

    NameServer get(long id);

}
