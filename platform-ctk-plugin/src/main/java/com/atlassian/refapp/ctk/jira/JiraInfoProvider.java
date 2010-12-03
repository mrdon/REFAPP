package com.atlassian.refapp.ctk.jira;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.google.common.collect.Sets;

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

    public String getAdminFullname()
    {
        return "Administrator";
    }

    public String getMatchingSearchTerm()
    {
        return "jira";
    }

    public Set<String> getExpectedMatchingContents()
    {
        return Sets.newHashSet("jira");
    }
}