package com.atlassian.refapp.ctk;

import java.util.Set;
import javax.ws.rs.core.UriBuilder;

/**
 * Implementations of this provide application-specific information for test purpose.
 */
public interface AppSpecificInfoProvider
{
    /**
     * @return username which can be used for logging into app as admin.
     */
    String getAdminUsername();

    /**
     * @return password which can be used for logging into app as admin.
     */
    String getAdminPassword();

    /**
     * @return the fullname of admin as stored within the app.
     */
    String getAdminFullname();

    /**
     * @return a search term which guarantees at least a match, or <code>null</code> if searching is not supported.
     */
    public String getMatchingSearchTerm();

    /**
     * @return contents (keywords, url components, or excerpts) expected within results given the search is performed
     * using {@link #getMatchingSearchTerm()}, or <code>null</code> if searching is not supported.
     */
    public Set<String> getExpectedMatchingContents();

    /**
     * @return a valid license key.
     */
    public String getValidLicense();
}