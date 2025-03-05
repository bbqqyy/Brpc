package com.bqy.provider;


import com.bqy.model.User;
import com.bqy.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }
}
