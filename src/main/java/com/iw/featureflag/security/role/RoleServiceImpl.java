package com.iw.featureflag.security.role;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.iw.featureflag.security.config.FirebaseConfig;
import com.iw.featureflag.security.models.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final FirebaseAuth firebaseAuth;
    private final SecurityProperties securityProps;

    @Autowired
    public RoleServiceImpl(final SecurityProperties securityProps, final FirebaseConfig firebaseConfig) throws IOException {
        this.firebaseAuth = firebaseConfig.getAuth();
        this.securityProps = securityProps;
    }

    @Override
    public void addRole(final String uid, final String role) throws Exception {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>();
            user.getCustomClaims().forEach(claims::put);
            if (securityProps.getValidApplicationRoles().contains(role)) {
                if (!claims.containsKey(role)) {
                    claims.put(role, true);
                }
                firebaseAuth.setCustomUserClaims(uid, claims);
            } else {
                throw new Exception("Not a valid Application role, Allowed roles => "
                        + securityProps.getValidApplicationRoles().toString());
            }

        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }

    }

    @Override
    public void removeRole(String uid, String role) {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>();
            user.getCustomClaims().forEach((k, v) -> claims.put(k, v));
            if (claims.containsKey(role)) {
                claims.remove(role);
            }
            firebaseAuth.setCustomUserClaims(uid, claims);
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }

}
