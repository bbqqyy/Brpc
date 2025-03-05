package com.bqy.examplespringbootprovider;

import com.bqy.model.User;
import com.bqy.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名："+user.getName());
        return user;
    }
}
