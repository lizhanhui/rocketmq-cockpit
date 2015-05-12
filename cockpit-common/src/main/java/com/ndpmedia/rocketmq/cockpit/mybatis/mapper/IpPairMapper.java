package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.IPPair;

import java.util.List;

public interface IpPairMapper {

    List<IPPair> list();

    String lookUp(String innerIP);

    long insert(IPPair ipPair);

    void delete(long id);

    void update(IPPair ipPair);
}
