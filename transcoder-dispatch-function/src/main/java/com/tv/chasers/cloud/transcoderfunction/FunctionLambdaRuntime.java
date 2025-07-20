package com.tv.chasers.cloud.transcoderfunction;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class FunctionLambdaRuntime extends AbstractMicronautLambdaRuntime<S3Event, Void, S3Event, Void>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionLambdaRuntime.class);

    public static void main(String[] args) {
        try {
            new FunctionLambdaRuntime().run(args);
        } catch (MalformedURLException e) {
            LOGGER.error("Bad URL", e);
        }
    }

    @Override
    @Nullable
    protected RequestHandler<S3Event, Void> createRequestHandler(String... args) {
        return new FunctionRequestHandler();
    }
}
