package com.iw.featureflag.security.service;

import com.iw.featureflag.security.models.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CookieService {

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final SecurityProperties restSecProps;

    @Autowired
    public CookieService(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SecurityProperties restSecProps) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.restSecProps = restSecProps;
    }

    public Cookie getCookie(String name) {
        return WebUtils.getCookie(httpServletRequest, name);
    }
}
