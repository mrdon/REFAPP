package com.atlassian.refapp.ctk.bamboo;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;

import java.util.Set;

public class BambooInfoProvider implements AppSpecificInfoProvider {

    public String getAdminUsername() {
        return "admin";
    }

    public String getAdminPassword() {
        return "admin";
    }

    public String getAdminFullname() {
        return "Bamboo Master";
    }

    public String getMatchingSearchTerm() {
        // Search is not supported by Bamboo.
        return null;
    }

    public Set<String> getExpectedMatchingContents() {
        // Search is not supported by Bamboo.
        return null;
    }
}
