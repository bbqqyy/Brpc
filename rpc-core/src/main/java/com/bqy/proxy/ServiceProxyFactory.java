package com.bqy.proxy;

import com.bqy.RpcApplication;
import com.bqy.config.RpcConfig;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    public static <T> T newInstance(Class<T> serviceClass){
        if(RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

    private static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockProxy());
    }

}
