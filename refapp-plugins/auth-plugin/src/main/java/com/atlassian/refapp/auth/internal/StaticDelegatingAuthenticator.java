package com.atlassian.refapp.auth.internal;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;

public class StaticDelegatingAuthenticator implements Authenticator
{
    private static Authenticator authenticator;

    static void setAuthenticator(Authenticator authenticator)
    {
        StaticDelegatingAuthenticator.authenticator = authenticator;
    }
    
    public void init(Map params, SecurityConfig securityConfig)
    {
        authenticator.init(params, securityConfig);
    }

    public String getRemoteUser(HttpServletRequest request)
    {
        return authenticator.getRemoteUser(request);
    }

    public Principal getUser(HttpServletRequest request, HttpServletResponse response)
    {
        return authenticator.getUser(request, response);
    }

    public Principal getUser(HttpServletRequest request)
    {
        return authenticator.getUser(request);
    }

    public boolean isUserInRole(HttpServletRequest request, String role)
    {
        return authenticator.isUserInRole(request, role);
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean cookie)
            throws AuthenticatorException
    {
        return authenticator.login(request, response, username, password, cookie);
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticatorException
    {
        return authenticator.login(request, response, username, password);
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticatorException
    {
        return authenticator.logout(request, response);
    }

    public void destroy()
    {
        authenticator.destroy();
    }

}
