package com.iw.featureflag.security.models;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {
    private CookieProperties cookieProps;
    private FirebaseProperties firebaseProps;
    private boolean allowCredentials;
    private List<String> allowedOrigins;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private List<String> allowedMethods;
    private List<String> allowedPublicApis;
    List<String> superAdmins;
    List<String> admins;
    List<String> users;
    List<String> validApplicationRoles;
}
