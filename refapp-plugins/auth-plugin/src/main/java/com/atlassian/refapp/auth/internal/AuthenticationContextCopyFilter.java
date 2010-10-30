package com.atlassian.refapp.auth.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.config.SecurityConfig;

public class AuthenticationContextCopyFilter implements Filter
{
    private final AuthenticationContext authenticationContext;
    private SecurityConfig securityConfig;
    private final ThreadLocal<Integer> entryCount = new ThreadLocal<Integer>();
    
    public AuthenticationContextCopyFilter(AuthenticationContext authenticationContext)
    {
        this.authenticationContext = authenticationContext;
    }
    
    public void init(FilterConfig filterConfig) throws ServletException
    {
        securityConfig = (SecurityConfig) filterConfig.getServletContext().getAttribute(SecurityConfig.STORAGE_KEY);
        if (securityConfig == null)
        {
            throw new ServletException("No SecurityConfig found in servlet context!");
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        authenticationContext.setUser(securityConfig.getAuthenticationContext().getUser());
        entryCount.set(entryCount.get() == null ? 1 : entryCount.get() + 1);
        try
        {
            chain.doFilter(request, response);
        } finally
        {
            entryCount.set(entryCount.get() - 1);
            if (entryCount.get() == 0)
            {
                authenticationContext.clearUser();
            }
        }
    }

    public void destroy()
    {
    }
}
