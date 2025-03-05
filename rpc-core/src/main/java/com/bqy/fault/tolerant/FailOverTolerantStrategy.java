package com.bqy.fault.tolerant;

import com.bqy.model.RpcResponse;

import java.util.Map;

public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}
