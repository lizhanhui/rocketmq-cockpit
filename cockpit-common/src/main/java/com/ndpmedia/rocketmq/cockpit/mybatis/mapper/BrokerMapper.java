package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.BrokerLoad;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BrokerMapper {

    boolean exists(Broker broker);

    void insert(Broker broker);

    void refresh(Broker broker);

    List<Broker> list(@Param("clusterName") String clusterName,
                      @Param("brokerName") String brokerName,
                      @Param("brokerId") int brokerId,
                      @Param("dc") int dc,
                      @Param("lastUpdateTime") Date lastUpdateTime);

    List<BrokerLoad> queryBrokerLoad();

}
