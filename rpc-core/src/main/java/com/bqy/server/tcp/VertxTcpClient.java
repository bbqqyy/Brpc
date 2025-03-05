package com.bqy.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.bqy.RpcApplication;
import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.model.ServiceMetaInfo;
import com.bqy.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connect to TCP server");
                io.vertx.core.net.NetSocket netSocket = result.result();
                netSocket.write("hello server");
                netSocket.handler(buffer -> {
                    System.out.println("Received response from server" + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        client.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializerKey()).getKey());
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);
                    try {
                        Buffer resultBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(resultBuffer);
                    }catch (IOException e){
                        throw new RuntimeException("协议消息编码失败");
                    }
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try{
                            ProtocolMessage<RpcResponse> responseCompletableFuture = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            completableFuture.complete(responseCompletableFuture.getBody());
                        }catch (IOException e){
                            throw new RuntimeException("协议消息解码失败");
                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                });

            RpcResponse rpcResponse = completableFuture.get();
            client.close();
            return rpcResponse;

    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
