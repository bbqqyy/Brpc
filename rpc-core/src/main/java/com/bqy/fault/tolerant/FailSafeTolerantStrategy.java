package com.bqy.fault.tolerant;

import com.bqy.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默异常处理",e);
        return new RpcResponse();
    }
}
