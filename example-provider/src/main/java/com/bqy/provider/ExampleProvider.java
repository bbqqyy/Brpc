package com.bqy.provider;

import com.bqy.RpcApplication;
import com.bqy.config.RegistryConfig;
import com.bqy.config.RpcConfig;
import com.bqy.constant.RpcConstant;
import com.bqy.model.ServiceMetaInfo;
import com.bqy.register.LocalRegistry;
import com.bqy.register.Register;
import com.bqy.register.Registry;
import com.bqy.register.RegistryFactory;
import com.bqy.server.HttpServer;
import com.bqy.server.VertxHttpServer;
import com.bqy.server.tcp.VertxTcpServer;
import com.bqy.service.UserService;

public class ExampleProvider {
    public static void main(String[] args) {
        RpcApplication.init();
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName,UserServiceImpl.class);
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
//            Register.register(UserService.class.getName(),UserServiceImpl.class);
            //todo 提供服务
            HttpServer server = new VertxTcpServer();
            server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
