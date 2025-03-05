package com.bqy.model;

import com.bqy.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private String serviceName;

    private String methodName;

    private String version = RpcConstant.DEFAULT_SERVICE_VERSION;

    private Class<?>[] paramTypeList;


    private Object[] args;
}
