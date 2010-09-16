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
     * @return return base context of the app which we're testing against.
     */
    String getBaseContext();

    /**
     * @return return port of the app which we're testing against.
     */
    String getPort();

    /**
     * @return a search term which guarantees at least a match.
     */
    public String getMatchingSearchTerm();

    /**
     * @return urls which must match given the search is performed using {@link #getMatchingSearchTerm()}.
     */
    public Set<String> getExpectedMatchingUrls();

    Integer getPortValue();

    UriBuilder createLocalhostUriBuilder();
}
