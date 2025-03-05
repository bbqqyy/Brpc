package com.bqy.serializer;


import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;

public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T object = OBJECT_MAPPER.readValue(bytes,type);
        if(object instanceof RpcRequest){
            return handleRequest((RpcRequest)object,type);
        }
        if(object instanceof RpcResponse){
            return handleResponse((RpcResponse)object,type);
        }
        return object;
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type)throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes,rpcResponse.getDataType()));
        return type.cast(rpcResponse);

    }

    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException{
        Class<?>[] paramTypeList = rpcRequest.getParamTypeList();
        Object[] objects = rpcRequest.getArgs();
        for(int i = 0;i<paramTypeList.length;i++){
            Class<?> clazz = paramTypeList[i];
            if(!clazz.isAssignableFrom(objects[i].getClass())){
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(objects[i]);
                objects[i] = OBJECT_MAPPER.readValue(bytes,clazz);
            }
        }
        return type.cast(rpcRequest);
    }
}
