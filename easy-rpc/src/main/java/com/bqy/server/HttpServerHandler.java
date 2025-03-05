package com.bqy.server;

import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.register.Register;
import com.bqy.serializer.JDKSerializer;
import com.bqy.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = new JDKSerializer();
        System.out.println(request.method()+request.uri());
            request.bodyHandler(body->{
                byte[] bytes = body.getBytes();
                RpcRequest rpcRequest = null;
                try {
                     rpcRequest = serializer.deserialize(bytes,RpcRequest.class);
                }catch (IOException e){
                    e.printStackTrace();
                }
                RpcResponse rpcResponse = new RpcResponse();
                if(rpcRequest==null){
                    System.out.println("rpcRequest is null");
                    doResponse(request,rpcResponse,serializer);
                    return;
                }
                try {
                    Class<?> implClass = Register.get(rpcRequest.getServiceName());
                    Method method = implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypeList());
                    Object result = method.invoke(implClass.newInstance(),rpcRequest.getArgs());

                    rpcResponse.setData(result);
                    rpcResponse.setDataType(method.getReturnType());
                    rpcResponse.setMessage("ok");
                }catch (Exception e){
                    e.printStackTrace();
                    rpcResponse.setException(e);
                    rpcResponse.setMessage(e.getMessage());
                }
                doResponse(request,rpcResponse,serializer);
            });

    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response()
                .putHeader("content-type","application/json");
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(bytes));
        }catch (IOException e){
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
