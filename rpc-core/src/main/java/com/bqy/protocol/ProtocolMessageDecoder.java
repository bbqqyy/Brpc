package com.bqy.protocol;

import com.bqy.model.RpcRequest;
import com.bqy.model.RpcResponse;
import com.bqy.serializer.Serializer;
import com.bqy.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException{
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic  = buffer.getByte(0);
        if(magic != ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("消息magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        int bodyLength = header.getBodyLength();
        if (bodyLength < 0) {
            throw new IllegalArgumentException("bodyLength 不能小于 0");
        }

        int start = 17;
        int end = start + bodyLength;
        if (end > buffer.length()) {
            throw new IllegalArgumentException("end 超出 Buffer 长度");
        }
        if (end < start) {
            throw new IllegalArgumentException("end 不能小于 start");
        }

        byte[] bodyBytes = buffer.getBytes(start, end);
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(protocolMessageSerializerEnum==null){
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(protocolMessageSerializerEnum.getValue());
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(protocolMessageTypeEnum==null){
            throw new RuntimeException("请求类型不存在");
        }
        switch (protocolMessageTypeEnum){
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes,RpcRequest.class);
                return new ProtocolMessage<>(header,request);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes,RpcResponse.class);
                return new ProtocolMessage<>(header,rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该类型");
        }
    }

}
