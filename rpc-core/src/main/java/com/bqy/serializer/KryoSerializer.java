package com.bqy.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer{
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output,object);
        output.close();
        return outputStream.toByteArray();

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input,type);
        input.close();
        return result;
    }
}
