package com.bqy.provider;

import com.bqy.register.Register;
import com.bqy.server.HttpServer;
import com.bqy.server.VertxHttpServer;
import com.bqy.service.UserService;


public class EasyProviderExample {
    public static void main(String[] args) {
        Register.register(UserService.class.getName(),UserServiceImpl.class);
        //todo 提供服务
        HttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
