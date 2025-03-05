package com.bqy.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.model.User;
import com.bqy.serializer.JdkSerializer;
import com.bqy.serializer.Serializer;
import com.bqy.service.UserService;

import java.io.IOException;

public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .paramTypeList(new Class[]{User.class})
                .args(new Object[]{user})
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
            return (User) rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
