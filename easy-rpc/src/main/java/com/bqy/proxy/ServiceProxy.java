package com.bqy.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.serializer.JDKSerializer;
import com.bqy.serializer.Serializer;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JDKSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypeList(method.getParameterTypes())
                .args(args)
                .build();
        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (
                    HttpResponse response = HttpRequest.post("http://localhost:8080")
                            .body(bodyBytes)
                            .execute()){

                result = response.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result,RpcResponse.class);
            return  rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
