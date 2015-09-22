package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
public class BrokerScheduler {

    @Autowired
    private BrokerMapper brokerMapper;

    /**
     * Check broker status every 30 minutes.
     */
    @Scheduled(fixedRate = 1800000)
    public void checkBrokerStatus() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();
            ClusterInfo clusterInfo = defaultMQAdminExt.examineBrokerClusterInfo();

            Map<String, Set<String>> clusterAddrTable = clusterInfo.getClusterAddrTable();
            Map<String, BrokerData> brokerDataMap = clusterInfo.getBrokerAddrTable();

            if (null != clusterAddrTable && !clusterAddrTable.isEmpty()) {
                for (Map.Entry<String, Set<String>> entry : clusterInfo.getClusterAddrTable().entrySet()) {
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
        } finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
