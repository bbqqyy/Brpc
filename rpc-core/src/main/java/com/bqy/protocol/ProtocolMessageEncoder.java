package com.bqy.protocol;

import com.bqy.serializer.Serializer;
import com.bqy.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException{
        if(protocolMessage==null||protocolMessage.getHeader()==null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(protocolMessageSerializerEnum==null){
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(protocolMessageSerializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendBytes(bodyBytes);
        buffer.appendInt(header.getBodyLength());
        return buffer;
    }
}
