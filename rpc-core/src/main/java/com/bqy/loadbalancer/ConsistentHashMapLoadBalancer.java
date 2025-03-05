package com.bqy.loadbalancer;

import com.bqy.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashMapLoadBalancer implements LoadBalancer{
    private final TreeMap<Integer,ServiceMetaInfo> treeMap = new TreeMap<>();
    private static final int VIRTUAL_NODE_NUM = 100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        for(ServiceMetaInfo serviceMetaInfo:serviceMetaInfoList){
            for(int i = 0;i<VIRTUAL_NODE_NUM;i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                treeMap.put(hash,serviceMetaInfo);
            }
        }
        int hash = getHash(requestParams);
        Map.Entry<Integer,ServiceMetaInfo> entry = treeMap.ceilingEntry(hash);
        if(entry==null){
            entry = treeMap.firstEntry();
        }
        return entry.getValue();

    }
    private int getHash(Object key){
        return key.hashCode();
    }
}
