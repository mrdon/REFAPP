package com.atlassian.refapp.ctk.refapp;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.google.common.collect.Sets;

import java.util.Set;

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

    public String getAdminFullname()
    {
        return "A. D. Ministrator (Sysadmin)";
    }

    public String getMatchingSearchTerm()
    {
        return "refapp";
    }

    public Set<String> getExpectedMatchingContents()
    {
        return Sets.newHashSet("http://foo.com", "http://bar.com");
    }
}
