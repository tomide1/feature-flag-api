package com.iw.featureflag.security.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.iw.featureflag.model.Feature;
import com.iw.featureflag.security.config.FirebaseConfig;
import com.iw.featureflag.security.models.Credentials;
import com.iw.featureflag.security.models.SecurityProperties;
import com.iw.featureflag.security.models.User;
import com.iw.featureflag.security.role.RoleConstants;
import com.iw.featureflag.security.role.RoleService;
import com.iw.featureflag.security.service.CookieService;
import com.iw.featureflag.security.service.SecurityService;
import com.iw.featureflag.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final static Logger LOG = LoggerFactory.getLogger("SecurityFilter.class");

    private final SecurityService securityService;
    private final CookieService cookieUtils;
    private final SecurityProperties securityProps;
    private final RoleService securityRoleService;
    private final FirebaseAuth firebaseAuth;
    private final UserService userService;

    @Autowired
    public SecurityFilter(
            final SecurityService securityService,
            final CookieService cookieUtils,
            final SecurityProperties securityProps,
            final RoleService securityRoleService,
            final FirebaseConfig firebaseConfig,
            final UserService userService
    ) throws IOException {
        this.securityService = securityService;
        this.cookieUtils = cookieUtils;
        this.securityProps = securityProps;
        this.securityRoleService = securityRoleService;
        this.firebaseAuth = firebaseConfig.getAuth();
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        authorize(request);
        filterChain.doFilter(request, response);
    }

    private void authorize(final HttpServletRequest request) {
        String sessionCookieValue = null;
        FirebaseToken decodedToken = null;
        Credentials.CredentialType type = null;

        // Token verification
        boolean strictServerSessionEnabled = securityProps.getFirebaseProps().isEnableStrictServerSession();
        Cookie sessionCookie = cookieUtils.getCookie("session");
        String token = securityService.getBearerToken(request);
        try {
            if (sessionCookie != null) {
                sessionCookieValue = sessionCookie.getValue();
                decodedToken = firebaseAuth.verifySessionCookie(sessionCookieValue,
                        securityProps.getFirebaseProps().isEnableCheckSessionRevoked());
                type = Credentials.CredentialType.SESSION;
            } else if (!strictServerSessionEnabled && token != null && !token.equals("null")
                    && !token.equalsIgnoreCase("undefined")) {
                decodedToken = firebaseAuth.verifyIdToken(token);
                type = Credentials.CredentialType.ID_TOKEN;
            }
        } catch (FirebaseAuthException e) {
            LOG.error("Firebase Exception:: " + e.getLocalizedMessage());
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        User user = firebaseTokenToUserDto(decodedToken);
//        saveUserInDb(user);

        // Handle roles
        if (user != null) {
            // Handle Super Role
            if (securityProps.getSuperAdmins().contains(user.getEmail()) && decodedToken != null) {
                handleUserRole(decodedToken, authorities, RoleConstants.ROLE_SUPER);
            }

            //Handle Admin Role
            if (securityProps.getAdmins().contains(user.getEmail()) && decodedToken != null) {
                handleUserRole(decodedToken, authorities, RoleConstants.ROLE_ADMIN);
            }

            //Handle User Role
            if (securityProps.getUsers().contains(user.getEmail()) && decodedToken != null) {
                handleUserRole(decodedToken, authorities, RoleConstants.ROLE_USER);
            }

            // Handle Other roles
            if (decodedToken != null)
                decodedToken.getClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority(k)));

            // Set security context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    new Credentials(type, decodedToken, token, sessionCookieValue),
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private User firebaseTokenToUserDto(final FirebaseToken decodedToken) {
        User user = null;
        if (decodedToken != null) {
            user = new User();
            user.setUid(decodedToken.getUid());
            user.setName(decodedToken.getName());
            user.setEmail(decodedToken.getEmail());
            user.setFeatures(Feature.GLOBAL_FEATURES.stream().map(Feature::getId).collect(Collectors.toList()));
        }
        System.out.println(user);
        return user;
    }

    private void handleUserRole(FirebaseToken decodedToken, List<GrantedAuthority> authorities, String role) {
        if (!decodedToken.getClaims().containsKey(role)) {
            try {
                securityRoleService.addRole(decodedToken.getUid(), role);
            } catch (Exception e) {
                LOG.error("Role registration exception ", e);
            }
        }
        authorities.add(new SimpleGrantedAuthority(role));
    }

    private void saveUserInDb(User user) {
        userService.saveUser(user);
    }
}