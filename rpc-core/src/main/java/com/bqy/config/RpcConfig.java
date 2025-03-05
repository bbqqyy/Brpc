package com.bqy.config;

import com.bqy.fault.retry.RetryStrategyKeys;
import com.bqy.fault.tolerant.TolerantStrategyKeys;
import com.bqy.loadbalancer.LoadBalancerKeys;
import com.bqy.serializer.Serializer;
import com.bqy.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    private boolean mock = false;
    private String name = "rpc-study";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer serverPort = 8080;
    private String serializerKey = SerializerKeys.JDK;
    private RegistryConfig registryConfig = new RegistryConfig();
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    private String retryStrategy = RetryStrategyKeys.FIXEDINTERVAL;
    private String tolerateStrategy = TolerantStrategyKeys.FAIL_FAST;
}
