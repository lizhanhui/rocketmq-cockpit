package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service("cockpitBrokerMQService")
public class CockpitBrokerMQServiceImpl implements CockpitBrokerMQService {
    private static final int MAX_TIMEOUT_RETRY_TIMES = 5;

    private Logger logger = LoggerFactory.getLogger(CockpitBrokerMQServiceImpl.class);

    @Override
    public Set<String> getALLBrokers(DefaultMQAdminExt defaultMQAdminExt) {
        logger.info("[cockpit broker] try to get broker list");
        Set<String> brokerList = new HashSet<>();
        int flag = 0;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while (flag++ < MAX_TIMEOUT_RETRY_TIMES) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                break;
            } catch (Exception e) {
                logger.info("Failed to get cluster info", e);
            }
        }

        if (null != clusterInfoSerializeWrapper.getClusterAddrTable()) {
            Set<Map.Entry<String, Set<String>>> clusterSet =
                    clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

            for (Map.Entry<String, Set<String>> next : clusterSet) {
                Set<String> brokerNameSet = new HashSet<String>();
                brokerNameSet.addAll(next.getValue());

                for (String brokerName : brokerNameSet) {
                    BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                    if (brokerData != null) {
                        Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();
                        for (Map.Entry<Long, String> nextEntry : brokerAddrSet) {
                            if (nextEntry.getKey() != MixAll.MASTER_ID) {
                                logger.info("this broker is not master ." + nextEntry.getValue());
                                continue;
                            }
                            brokerList.add(nextEntry.getValue());
                        }
                    }
                }
            }
        }
        logger.info("[cockpit broker] now we get broker list, size : " + brokerList.size() + brokerList);
        return brokerList;
    }

    @Override
    public Map<String, String> getBrokerCluster(DefaultMQAdminExt defaultMQAdminExt) {
        Map<String, String> brokerToCluster = new HashMap<>();

        logger.info("[sync topic] try to get broker list");
        int flag = 0;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while (flag++ < 5) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != clusterInfoSerializeWrapper.getClusterAddrTable()) {
            Set<Map.Entry<String, Set<String>>> clusterSet =
                    clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

            for (Map.Entry<String, Set<String>> next : clusterSet) {
                Set<String> brokerNameSet = new HashSet<String>();
                brokerNameSet.addAll(next.getValue());
                String cluster = next.getKey();


                for (String brokerName : brokerNameSet) {
                    BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                    if (brokerData != null) {
                        Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();
                        for (Map.Entry<Long, String> item : brokerAddrSet) {
                            brokerToCluster.put(item.getValue(), cluster);
                        }
                    }
                }
            }
        }
        logger.info("[sync topic] now we get broker list, size : " + brokerToCluster.size() + " [] " + brokerToCluster);
        return brokerToCluster;
    }

    @Override
    public Set<String> getAllNames(DefaultMQAdminExt defaultMQAdminExt) {
        logger.info("[cockpit name] try to get cluster name, broker name list");
        Set<String> nameList = new HashSet<>();
        int flag = 0;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while (flag++ < 5) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != clusterInfoSerializeWrapper.getClusterAddrTable()) {
            Set<Map.Entry<String, Set<String>>> clusterSet =
                    clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

            for (Map.Entry<String, Set<String>> next : clusterSet) {
                nameList.addAll(next.getValue());
                nameList.add(next.getKey());

            }
        }
        logger.info("[cockpit name] now we get cluster name, broker name list , size : " + nameList.size() + nameList);
        return nameList;
    }

    @Override
    public boolean removeAllTopic(String broker) {
        try {

        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
