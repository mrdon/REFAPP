package com.atlassian.refapp.ctk.jira;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.google.common.collect.Sets;

import javax.ws.rs.core.UriBuilder;
import java.util.Set;

public class JiraInfoProvider implements AppSpecificInfoProvider
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
        return System.getProperty("context.path", "/jira");
    }

    public String getPort()
    {
        return System.getProperty("http.port", "5990");
    }

    public String getMatchingSearchTerm()
    {
        return "jira";
    }

    public Set<String> getExpectedMatchingContents()
    {
        return Sets.newHashSet("http://foo.com", "http://bar.com");
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