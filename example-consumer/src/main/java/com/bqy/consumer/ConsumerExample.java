package com.bqy.consumer;

import com.bqy.RpcApplication;
import com.bqy.config.RpcConfig;
import com.bqy.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println(rpcConfig.toString());
    }
}
