package com.atlassian.refapp.ctk.confluence;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.google.common.collect.Sets;

import javax.ws.rs.core.UriBuilder;
import java.util.Set;

public class ConfluenceInfoProvider implements AppSpecificInfoProvider
{
    public String getAdminUsername()
    {
        return "admin";
    }

    public String getAdminPassword()
    {
        return "admin";
    }

    public String getAdminFullname()
    {
        return "admin";
    }

    public String getBaseContext()
    {
        return System.getProperty("context.path", "/confluence");
    }

    public String getPort()
    {
        return System.getProperty("http.port", "1990");
    }

    public String getMatchingSearchTerm()
    {
        return "confluence";
    }

    public Set<String> getExpectedMatchingContents()
    {
        return Sets.newHashSet("A note to Confluence administrators", "Home");
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
