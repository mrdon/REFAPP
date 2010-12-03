package com.atlassian.refapp.ctk.confluence;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.google.common.collect.Sets;

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
        return "A. D. Ministrator";
    }

    public String getMatchingSearchTerm()
    {
        return "confluence";
    }

    public Set<String> getExpectedMatchingContents()
    {
        return Sets.newHashSet("confluence");
    }
}
