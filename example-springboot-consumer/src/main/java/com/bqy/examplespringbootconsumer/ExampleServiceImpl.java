package com.bqy.examplespringbootconsumer;

import com.bqy.model.User;
import com.bqy.rpc.springboot.starter.annotation.RpcReference;
import com.bqy.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("bqy");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
