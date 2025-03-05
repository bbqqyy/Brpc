package com.bqy.server.tcp;

import com.bqy.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParse(bufferHandler);
    }

    private RecordParser initRecordParse(Handler<Buffer> bufferHandler) {
        RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        recordParser.setOutput(new Handler<Buffer>() {
            int size = -1;
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if(-1==size){
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);
                }else {
                    resultBuffer.appendBuffer(buffer);
                    bufferHandler.handle(resultBuffer);

                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();

                }
            }
        });
        return recordParser;
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
