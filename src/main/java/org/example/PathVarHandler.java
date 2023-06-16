package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PathVarHandler implements RequestHandler<Map<String,Object>, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> input, Context context) {
        Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
        String path = "basic";
        String redirectLocation = "https://google.com";

        if (pathParameters != null) {
            context.getLogger().log("Path variable " + pathParameters.get("data"));
            path = pathParameters.get("data");
        }

        if(path.equals("abc")){
            redirectLocation = "https://openai.com";
        }
        else if (path.equals("def")){
            redirectLocation = "https://amazon.com";
        }


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
