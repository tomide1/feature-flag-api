package com.iw.featureflag.controller;

import com.iw.featureflag.model.Feature;
import com.iw.featureflag.security.service.SecurityService;
import com.iw.featureflag.service.FeatureService;
import com.iw.featureflag.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureControllerTest {

    @Mock
    private SecurityService mockSecurityService;

    @Mock
    private FeatureService mockFeatureService;

    @Mock
    private UserService mockUserService;

    @Mock
    private Feature mockFeature;

    @Test
    public void createFeature_shouldReturnUnauthorized_ForNonAdminUser() {
        FeatureController featureController = new FeatureController(mockSecurityService, mockFeatureService, mockUserService);
        when(mockSecurityService.getAuthorities()).thenReturn(Collections.emptyList());

        ResponseEntity response = featureController.createFeature(mockFeature);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}