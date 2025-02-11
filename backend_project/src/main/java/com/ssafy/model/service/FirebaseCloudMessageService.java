package com.ssafy.model.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.ssafy.model.entity.Constants;
import com.ssafy.model.entity.message.FcmMessage;
import com.ssafy.model.entity.message.Message;
import com.ssafy.model.entity.message.Notification;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/" + Constants.FIREBASE_PROJECT_ID + "/messages:send";
    private final ObjectMapper objectMapper;

    public FirebaseCloudMessageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(Constants.FIREBASE_KEY_FILE).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();

        return token;
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        Notification notification = new Notification(title, body, null);
        Message message = new Message(notification, targetToken);
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String makeMessageWithData(String targetToken, String title, String body, Map<String, String> data) throws JsonProcessingException {
        Notification notification = new Notification(title, body, null);
        Message message = new Message(notification, targetToken);
        message.putAllData(data);  // 데이터 추가
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        // 응답 로그 출력 (필요에 따라 주석 처리 가능)
        System.out.println(response.body().string());
    }

    public void sendMessageWithData(String targetToken, String title, String body, Map<String, String> data) throws IOException {
        String message = makeMessageWithData(targetToken, title, body, data);
        System.out.println("Sending FCM message: " + message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            int responseCode = response.code();
            String responseBody = response.body().string();  // 한 번만 호출

            if (response.isSuccessful()) {
                System.out.println("FCM 메시지 전송 성공!");
                System.out.println("Response code: " + responseCode);
                System.out.println("Response body: " + responseBody);
            } else {
                System.out.println("FCM 메시지 전송 실패!");
                System.out.println("Response code: " + responseCode);
                System.out.println("Error message: " + responseBody);
            }
        }
    }

}