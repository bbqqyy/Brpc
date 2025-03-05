
package com.bqy.fault.tolerant;

import com.bqy.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
