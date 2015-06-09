package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by robert on 2015/6/9.
 */
@Service("cockpitBrokerService")
public class CockpitBrokerServiceImpl implements CockpitBrokerService {

    @Override
    public Set<String> getALLBrokers(DefaultMQAdminExt defaultMQAdminExt) {
        System.out.println("[cockpit broker] try to get broker list");
        Set<String> brokerList = new HashSet<>();
        boolean flag = true;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while(flag) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                flag = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Set<Map.Entry<String, Set<String>>> clusterSet =
                clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

        for (Map.Entry<String, Set<String>> next : clusterSet) {
            Set<String> brokerNameSet = new HashSet<String>();
            brokerNameSet.addAll(next.getValue());

            for (String brokerName : brokerNameSet) {
                BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                if (brokerData != null) {
                    Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();
                    Iterator<Map.Entry<Long, String>> itAddr = brokerAddrSet.iterator();

                    while (itAddr.hasNext()) {
                        Map.Entry<Long, String> next1 = itAddr.next();
                        brokerList.add(next1.getValue());
                    }
                }
            }
        }

        System.out.println("[cockpit broker] now we get broker list , size : " + brokerList.size() + brokerList);
        return brokerList;
    }

    @Override
    public Map<String, String> getBrokerCluster(DefaultMQAdminExt defaultMQAdminExt) {
        Map<String, String> brokerToCluster = new HashMap<>();

        System.out.println("[sync topic] try to get broker list");
        boolean flag = true;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while(flag) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                flag = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
                    Iterator<Map.Entry<Long, String>> itAddr = brokerAddrSet.iterator();

                    while (itAddr.hasNext()) {
                        Map.Entry<Long, String> next1 = itAddr.next();
                        brokerToCluster.put(next1.getValue(), cluster);
                    }
                }
            }
        }

        System.out.println("[sync topic] now we get broker list , size : " + brokerToCluster.size() + " [] " + brokerToCluster);
        return brokerToCluster;
    }
}
