package com.bqy.loadbalancer;

import com.bqy.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer{
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        int size = serviceMetaInfoList.size();
        if(size==1){
            return serviceMetaInfoList.get(0);
        }
        int current = currentIndex.getAndIncrement()%size;
        return serviceMetaInfoList.get(current);
    }
}
