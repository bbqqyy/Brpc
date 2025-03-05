package com.bqy.provider;

import com.bqy.bootstrap.ProviderBootStrap;
import com.bqy.model.ServiceRegisterInfo;
import com.bqy.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {

    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(),UserServiceImpl.class);
        registerInfoList.add(serviceRegisterInfo);

        ProviderBootStrap.init(registerInfoList);
    }
}
