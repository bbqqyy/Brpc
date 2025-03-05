package com.bqy.rpc.springboot.starter.annotation;

import com.bqy.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.bqy.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.bqy.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstrap.class, RpcInitBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {
    boolean needService() default true;
}
