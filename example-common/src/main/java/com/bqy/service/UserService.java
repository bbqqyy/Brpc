package com.bqy.service;

import com.bqy.model.User;

public interface UserService {
    User getUser(User user);
    default int getNumber(){
        return 1;
    }
}
