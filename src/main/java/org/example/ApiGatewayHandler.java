package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApiGatewayHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        context.getLogger().log("Path : " + input.getPath());

        if (input.getPathParameters() != null) {
            context.getLogger().log("Path variable " + input.getPathParameters().get("key"));
        }

        // 리디렉션 처리 로직 작성
        String redirectLocation = "https://google.com";  // 리디렉션할 URL

        context.getLogger().log("Success #1 " + LocalDateTime.now());

        // 리디렉션 응답 생성
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(302);
        response.setHeaders(createRedirectHeaders(redirectLocation));
        response.setBody("");

        context.getLogger().log("Success #2 " + LocalDateTime.now());


        return response;
    }

    private static Map<String, String> createRedirectHeaders(String redirectLocation) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", redirectLocation);
        return headers;
    }
}
