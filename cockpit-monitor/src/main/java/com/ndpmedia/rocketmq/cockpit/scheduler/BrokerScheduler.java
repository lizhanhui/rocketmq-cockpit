package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.google.common.base.Preconditions;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
public class BrokerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerScheduler.class);

    @Autowired
    private BrokerMapper brokerMapper;

    private DefaultMQAdminExt defaultMQAdminExt;

    public BrokerScheduler() {
        defaultMQAdminExt = new DefaultMQAdminExt(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();
        } catch (MQClientException e) {
            LOGGER.warn("Failed to start defaultMQAdminExt", e);
        }
    }

    /**
     * Check broker status every 5 minutes.
     */
    @Scheduled(fixedRate = 300000)
    public void syncBrokerStatus() {
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
                                broker.setDc(parseDC(brokerName));
                                broker.setBrokerId(brokerEntry.getKey().intValue());
                                broker.setAddress(brokerEntry.getValue());
                                broker.setCreateTime(new Date());
                                broker.setUpdateTime(new Date());
                                if (!brokerMapper.exists(broker)) {
                                    brokerMapper.insert(broker);
                                } else {
                                    brokerMapper.refresh(broker.getId(), broker.getAddress());
                                }
                            }

                        }
                    }
                }
            }

        } catch (Throwable e) {
            LOGGER.warn("Failed to update broker status", e);
        }
    }

    /**
     * A sample broker name is: DefaultCluster_1_broker1.
     * @param brokerName Broker name.
     * @return DC inferred from broker name.
     */
    private int parseDC(String brokerName) {
        Preconditions.checkNotNull(brokerName);
        Preconditions.checkArgument(!brokerName.trim().isEmpty());

        String[] segments = brokerName.split("_");
        if (segments.length < 3) {
            LOGGER.warn("Broker Name is not normalized. If it's developing environment, please ignore this warning; otherwise, please contact admin to fix this issue");
            return 100;
        }
        return Integer.parseInt(segments[1]);
    }
}
