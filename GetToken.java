import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GetToken {
    public static void main(String[] args) throws IOException {

        JsonNode result = getToken("Client Id", "Client Secret");
        String accessToken = result.get("access_token").asText();
    }


    public static JsonNode getToken(String clientId, String clientSecret) throws IOException {
        // clientId and clientSecret를 Base64로 인코딩
        Base64.Encoder encoder = Base64.getEncoder();
        String basicAuth = encoder.encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes(StandardCharsets.UTF_8));

        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        // Request Body 구성
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("scope", "itsm")
                .build();

        // Request 생성
        Request.Builder builder = new Request.Builder()
                .url("<Domain>/oauth2/token")
                .addHeader("Authorization", "Basic " + basicAuth)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody);
        Request request = builder.build();

        // Request 수행
        try (Response execute = client.newCall(request).execute()) {
            if (!execute.isSuccessful()) {
                throw new IOException("Unexpected code " + execute);
            }
            ResponseBody body = execute.body();
            if (body == null) {
                throw new IOException("Response body is null");
            }

            return objectMapper.readTree(body.string());
        }
    }
}