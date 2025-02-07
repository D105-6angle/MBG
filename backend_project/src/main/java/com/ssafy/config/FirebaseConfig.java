package com.ssafy.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.ssafy.model.entity.FirebaseProperties;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;

@Configuration
public class FirebaseConfig {

    @Autowired
    private FirebaseProperties firebaseProperties;

    @PostConstruct
    public void initialize() {
        try {
            JSONObject serviceAccount = new JSONObject();
            serviceAccount.put("type", firebaseProperties.getType());
            serviceAccount.put("project_id", firebaseProperties.getProjectId());
            serviceAccount.put("private_key_id", firebaseProperties.getPrivateKeyId());
            serviceAccount.put("private_key", firebaseProperties.getPrivateKey());
            serviceAccount.put("client_email", firebaseProperties.getClientEmail());
            serviceAccount.put("client_id", firebaseProperties.getClientId());
            serviceAccount.put("auth_uri", firebaseProperties.getAuthUri());
            serviceAccount.put("token_uri", firebaseProperties.getTokenUri());
            serviceAccount.put("auth_provider_x509_cert_url", firebaseProperties.getAuthProviderX509CertUrl());
            serviceAccount.put("client_x509_cert_url", firebaseProperties.getClientX509CertUrl());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(serviceAccount.toString().getBytes())))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
