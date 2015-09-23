package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
public class BrokerScheduler {

    @Autowired
    private BrokerMapper brokerMapper;

    private DefaultMQAdminExt defaultMQAdminExt;

    public BrokerScheduler() {
        defaultMQAdminExt = new DefaultMQAdminExt(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check broker status every 5 minutes.
     */
    @Scheduled(fixedRate = 300000)
    public void checkBrokerStatus() {
        try {
            ClusterInfo clusterInfo = defaultMQAdminExt.examineBrokerClusterInfo();

            Map<String /* Cluster */, Set<String> /* Broker Name */> clusterBrokerTable =
                    clusterInfo.getClusterAddrTable();
            Map<String /* Broker Name */, BrokerData> brokerDataMap = clusterInfo.getBrokerAddrTable();

            if (null != clusterBrokerTable && !clusterBrokerTable.isEmpty()) {
                for (Map.Entry<String, Set<String>> entry : clusterBrokerTable.entrySet()) {
                    String clusterName = entry.getKey();
                    TreeSet<String> brokerNameSet = new TreeSet<String>();
                    brokerNameSet.addAll(entry.getValue());

                    for (String brokerName : brokerNameSet) {
                        BrokerData brokerData = brokerDataMap.get(brokerName);
                        if (null != brokerData) {
                            for (Map.Entry<Long, String> brokerEntry : brokerData.getBrokerAddrs().entrySet()) {
                                Broker broker = new Broker();
                                broker.setClusterName(clusterName);
                                broker.setBrokerName(brokerName);
                                broker.setBrokerId(brokerEntry.getKey().intValue());
                                broker.setAddress(brokerEntry.getValue());
                                broker.setCreateTime(new Date());
                                broker.setUpdateTime(new Date());
                                if (!brokerMapper.exists(broker)) {
                                    brokerMapper.insert(broker);
                                } else {
                                    brokerMapper.refresh(broker);
                                }
                            }

                        }
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
