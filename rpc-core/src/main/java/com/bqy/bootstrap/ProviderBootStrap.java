package com.bqy.bootstrap;

import com.bqy.RpcApplication;
import com.bqy.config.RegistryConfig;
import com.bqy.config.RpcConfig;
import com.bqy.model.ServiceMetaInfo;
import com.bqy.model.ServiceRegisterInfo;
import com.bqy.register.LocalRegistry;
import com.bqy.register.Registry;
import com.bqy.register.RegistryFactory;
import com.bqy.server.tcp.VertxTcpServer;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class ProviderBootStrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for(ServiceRegisterInfo<?> serviceRegisterInfo:serviceRegisterInfoList){
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            try {
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName+"注册失败",e);
            }

            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }
    }
}
