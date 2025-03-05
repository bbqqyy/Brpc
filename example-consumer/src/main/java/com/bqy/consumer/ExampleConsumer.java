package com.bqy.consumer;

import com.bqy.bootstrap.ConsumerBootStrap;
import com.bqy.proxy.ServiceProxyFactory;
import com.bqy.model.User;
import com.bqy.service.UserService;

public class ExampleConsumer {
    public static void main(String[] args) {
        ConsumerBootStrap.init();
        UserService userService = ServiceProxyFactory.newInstance(UserService.class);
        User user = new User();
        user.setName("com/bqy");
        User newUser = userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("user==null");
        }
        int number = userService.getNumber();
        System.out.println(number);

    }
}
