package com.atlassian.refapp.ctk.fecru;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;

import java.util.HashSet;
import java.util.Set;

public class FecruInfoProvider implements AppSpecificInfoProvider {

    public String getAdminUsername() {
        return "admin";
    }

    public String getAdminPassword() {
        return "admin";
    }

    public String getAdminFullname() {
        return "A. D. Ministrator";
    }

    public String getMatchingSearchTerm() {
        return "InputImportBug.java";
    }

    public Set<String> getExpectedMatchingContents() {
        return new HashSet<String>();
    }

}
