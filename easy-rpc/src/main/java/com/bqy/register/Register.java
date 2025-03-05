package com.bqy.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Register {

    private static final Map<String,Class<?>> map = new ConcurrentHashMap<>();

    public static void register(String className,Class<?> implClass){
        map.put(className,implClass);
    }
    public static Class<?> get(String className){
        return map.get(className);
    }
    public static void remove(String className){
        map.remove(className);
    }
}
