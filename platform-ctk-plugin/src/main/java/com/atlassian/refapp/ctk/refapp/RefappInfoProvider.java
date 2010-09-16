package com.atlassian.refapp.ctk.refapp;

import javax.ws.rs.core.UriBuilder;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;

/**
 * Info provider for refapp which has "admin"/"admin" user pass as default.
 */
public class RefappInfoProvider implements AppSpecificInfoProvider
{
    public String getAdminUsername()
    {
        return "admin";
    }

    public String getAdminPassword()
    {
        return "admin";
    }

    public String getBaseContext()
    {
        return System.getProperty("context.path", "/refapp");
    }

    public String getPort()
    {
        return System.getProperty("http.port", "5990");
    }

    public Integer getPortValue()
    {
        return Integer.parseInt(getPort());
    }

    public UriBuilder createLocalhostUriBuilder()
    {
        return UriBuilder.fromUri("http://localhost/").port(getPortValue()).path(getBaseContext());
    }
}
