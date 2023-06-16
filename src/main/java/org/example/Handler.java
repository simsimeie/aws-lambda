package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<String ,String> {
    @Override
    public String handleRequest(String input, Context context) {
        context.getLogger().log("Lambda 함수 실행됨");
        return "Hello World";
    }
}
