package com.bqy.rpc.springboot.starter.bootstrap;

import com.bqy.proxy.ServiceProxyFactory;
import com.bqy.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for(Field field:fields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);

            if(rpcReference!=null){
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass==void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.newInstance(interfaceClass);
                try {
                    field.set(bean,proxyObject);
                    field.setAccessible(false);
                }catch (IllegalAccessException e){
                    throw new RuntimeException("为字段注入代理失败",e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
