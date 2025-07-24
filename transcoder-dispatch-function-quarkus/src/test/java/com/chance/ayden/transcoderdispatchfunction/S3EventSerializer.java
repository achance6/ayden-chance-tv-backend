package com.chance.ayden.transcoderdispatchfunction;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

import java.io.*;

public class S3EventSerializer implements ObjectMapper {
    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        if (!LambdaEventSerializers.isLambdaSupportedEvent(context.getObjectToSerialize().getClass().getName())) {
            throw new IllegalArgumentException("Attempt to use Lambda events serializer on non-lambda class");
        }

        var serializer = LambdaEventSerializers.serializerFor(S3Event.class, ClassLoader.getSystemClassLoader());

        var outputStream = new ByteArrayOutputStream();

        serializer.toJson((S3Event) context.getObjectToSerialize(), outputStream);

        try (var in = new ByteArrayInputStream(outputStream.toByteArray())) {
            return in;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
