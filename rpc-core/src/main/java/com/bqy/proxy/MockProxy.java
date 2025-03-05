package com.bqy.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
@Slf4j
public class MockProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> methodReturnType) {
        if(methodReturnType.isPrimitive()){
            if(methodReturnType == int.class){
                return 0;
            }
            if(methodReturnType == boolean.class){
                return false;
            }
            if(methodReturnType == long.class){
                return 0L;
            }
            if(methodReturnType == short.class){
                return (short)0;
            }

        }
        return null;

    }
}
