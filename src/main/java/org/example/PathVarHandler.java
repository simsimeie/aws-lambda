package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PathVarHandler implements RequestHandler<Map<String,Object>, APIGatewayProxyResponseEvent> {
    private static final String URL = "jdbc:postgresql://database-2.cbs7c8887131.ap-northeast-2.rds.amazonaws.com:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> input, Context context) {
        Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
        String path = "basic";
        String redirectLocation = "https://naver.com";

        if (pathParameters != null) {
            context.getLogger().log("Path variable " + pathParameters.get("data"));
            path = pathParameters.get("data");
        }

        String selectSQL = "SELECT origin_url FROM url WHERE short_url = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, path);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        // 이 부분에 각 컬럼에 대한 처리를 넣어주세요.
                        // 예를 들어, String data = rs.getString("columnName");
                        redirectLocation = rs.getString("origin_url");
                        context.getLogger().log("## 쿼리조회성공 " + redirectLocation);

                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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
