package com.bqy.register;

import com.bqy.config.RegistryConfig;
import com.bqy.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    void heartBeat();
    void init(RegistryConfig registryConfig);
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    void unRegister(ServiceMetaInfo serviceMetaInfo);
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
    void destroy();
    void watch(String key);

}
