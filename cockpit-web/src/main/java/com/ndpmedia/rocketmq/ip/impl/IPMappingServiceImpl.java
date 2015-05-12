package com.ndpmedia.rocketmq.ip.impl;

import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.IpPairMapper;
import com.ndpmedia.rocketmq.ip.IPMappingService;
import org.springframework.beans.factory.annotation.Autowired;

public class IPMappingServiceImpl implements IPMappingService {

    @Autowired
    private IpPairMapper ipPairMapper;

    @Override
    public String lookUp(String innerIP) {
        return ipPairMapper.lookUp(innerIP);
    }
}
