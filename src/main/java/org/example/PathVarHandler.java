package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PathVarHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String ENDPOINT_PRD = "redis://elasticache-for-lambda-0001-001.p8jaip.0001.apn2.cache.amazonaws.com:6379";
    private static final String ENDPOINT_DEV = "redis://elasticache-for-lambda-0001-001.p8jaip.0001.apn2.cache.amazonaws.com:6379";
    private static final String profile = "profile";
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String pathVariable = null;
        String originUrl = null;


        long startTime = System.currentTimeMillis();

        String endPoint = System.getenv(profile).equals("DEV") ? ENDPOINT_DEV : ENDPOINT_PRD;
        context.getLogger().log("## Active Profile : " + System.getenv(profile));

        // 요청 Path 추출
        pathVariable = request.getPath();
        context.getLogger().log("Request Path: " + pathVariable);


        if (pathVariable != null) {
            try (RedisClient redisClient = RedisClient.create(endPoint);
                 StatefulRedisConnection<String, String> connection = redisClient.connect()) {
                RedisCommands<String, String> syncCommands = connection.sync();
                originUrl = syncCommands.get(pathVariable);
                context.getLogger().log("## Origin URL : " + originUrl.substring(0));
            } catch (Exception e) {
                context.getLogger().log("## Exception Msg : " + e.getMessage() + " Caused by : " + e.getCause());
                originUrl = "https://kyobo.com";
            }
        }

        // 리디렉션 응답 생성
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(302);
        response.setHeaders(createRedirectHeaders(originUrl));
        response.setBody("");

        long endTime = System.currentTimeMillis();

        context.getLogger().log("Elapsed Time : " + (endTime - startTime) + " ms");

        return response;
    }

    private static Map<String, String> createRedirectHeaders(String redirectLocation) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", redirectLocation);
        return headers;
    }
}
