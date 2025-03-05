package com.bqy.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    public static <T> T newInstance(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
