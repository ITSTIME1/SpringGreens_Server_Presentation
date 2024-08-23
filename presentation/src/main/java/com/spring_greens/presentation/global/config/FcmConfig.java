package com.spring_greens.presentation.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.spring_greens.presentation.fcm.exception.FcmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.sdk.key.path}")
    private String fcmSdkInitializeKeyPath;

    @Bean
    public FirebaseApp fcmAdminSdkInitialize()  {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream(fcmSdkInitializeKeyPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }

        } catch (FileNotFoundException e) {
            log.error("Error firebase not found : {}", e.getMessage(), e);
            throw new FcmException.FcmFileNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new FcmException.FcmIOException(e.getMessage());
        }
    }
}
