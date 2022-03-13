package com.iw.featureflag.controller;

import com.iw.featureflag.model.Feature;
import com.iw.featureflag.security.models.User;
import com.iw.featureflag.security.role.RoleConstants;
import com.iw.featureflag.security.service.SecurityService;
import com.iw.featureflag.service.FeatureService;
import com.iw.featureflag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feature")
public class FeatureController {

    private SecurityService securityService;
    private FeatureService featureService;
    private UserService userService;

    @Autowired
    public FeatureController(
            final SecurityService securityService,
            final FeatureService featureService,
            final UserService userService) {
        this.securityService = securityService;
        this.featureService = featureService;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity createFeature(@RequestBody final Feature feature) {
        if (userIsAdmin()) {
            String response = featureService.saveFeature(feature);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/on")
    public ResponseEntity switchFeatureOnForUser(
            @RequestParam("userId") final String userId,
            @RequestParam("featureId") final String featureId) {
        if (userIsAdmin()) {
            User user = userService.getUser(userId);
            if (user != null) {
                user.getFeatures().add(featureId);
                String response = userService.saveUser(user);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping()
    public ResponseEntity getAllUserFeatures() {
        User user = securityService.getUser();
        if (user != null) {
            return new ResponseEntity<>(user.getFeatures().toString(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean userIsAdmin() {
        return securityService.getAuthorities().stream()
                .anyMatch(authority -> RoleConstants.ROLE_ADMIN.equals(authority.getAuthority()));
    }
}
