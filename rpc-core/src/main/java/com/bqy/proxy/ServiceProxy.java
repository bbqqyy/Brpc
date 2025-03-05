package com.bqy.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.bqy.RpcApplication;
import com.bqy.config.RegistryConfig;
import com.bqy.config.RpcConfig;
import com.bqy.constant.RpcConstant;
import com.bqy.fault.retry.RetryStrategy;
import com.bqy.fault.retry.RetryStrategyFactory;
import com.bqy.fault.tolerant.TolerantStrategy;
import com.bqy.fault.tolerant.TolerantStrategyFactory;
import com.bqy.loadbalancer.LoadBalancer;
import com.bqy.loadbalancer.LoadBalancerFactory;
import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.model.ServiceMetaInfo;
import com.bqy.protocol.*;
import com.bqy.register.Registry;
import com.bqy.register.RegistryFactory;
import com.bqy.serializer.Serializer;
import com.bqy.serializer.SerializerFactory;
import com.bqy.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializerKey());

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypeList(method.getParameterTypes())
                .args(args)
                .build();
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String,Object> map = new HashMap<>();
            map.put("method",rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(map,serviceMetaInfoList);
            //发送TCP请求
            RpcResponse rpcResponse = new RpcResponse();
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(()->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            }catch (Exception e){
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerateStrategy());
                tolerantStrategy.doTolerant(null,e);
            }


            return rpcResponse.getData();

    }
}
