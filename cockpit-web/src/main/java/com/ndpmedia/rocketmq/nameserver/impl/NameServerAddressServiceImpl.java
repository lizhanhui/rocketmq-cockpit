package com.ndpmedia.rocketmq.nameserver.impl;

import com.ndpmedia.rocketmq.cockpit.model.NameServer;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.NameServerMapper;
import com.ndpmedia.rocketmq.nameserver.NameServerAddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NameServerAddressServiceImpl implements NameServerAddressService {

    @Autowired
    private NameServerMapper nameServerMapper;

    @Override
    public String listNameServer() {
        StringBuilder stringBuilder = new StringBuilder(256);
        List<NameServer> nameServerList = nameServerMapper.list();
        if (null != nameServerList && nameServerList.size() > 0) {
            for (int i = 0; i < nameServerList.size(); i++) {
                NameServer nameServer = nameServerList.get(i);
                if (i > 0) {
                    stringBuilder.append(";");
                }
                stringBuilder.append(nameServer.getIp()).append(":").append(nameServer.getPort());
            }
        }

        return stringBuilder.toString();
    }
}
