package com.bqy.fault.retry;

import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
