package com.bqy;

import com.bqy.config.RegistryConfig;
import com.bqy.config.RpcConfig;
import com.bqy.constant.RpcConstant;
import com.bqy.register.Registry;
import com.bqy.register.RegistryFactory;
import com.bqy.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init,config = {}",rpcConfig.toString());
        RegistryConfig registryConfig = newRpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init , config={}",registryConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class,RpcConstant.RPC_CONFIG_PREFIX);
//            newRpcConfig = ConfigUtils.loadConfigYml();
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }
    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
