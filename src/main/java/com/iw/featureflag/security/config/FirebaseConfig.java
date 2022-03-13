package com.iw.featureflag.security.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.iw.featureflag.security.models.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class FirebaseConfig {
    private final SecurityProperties secProps;

    @Autowired
    public FirebaseConfig(final SecurityProperties secProps) {
        this.secProps = secProps;
    }

    @Primary
    @Bean
    public FirebaseApp getFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl(secProps.getFirebaseProps().getDatabaseUrl())
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getFirebaseApp());
    }

    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        return FirebaseDatabase.getInstance();
    }

    @Bean
    public Firestore getDatabase() throws IOException {
        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.getApplicationDefault()).build();
        return firestoreOptions.getService();
    }

    @Bean
    public FirebaseMessaging getMessaging() throws IOException {
        return FirebaseMessaging.getInstance(getFirebaseApp());
    }

    @Bean
    public FirebaseRemoteConfig getRemoteConfig() throws IOException {
        return FirebaseRemoteConfig.getInstance(getFirebaseApp());
    }
}
