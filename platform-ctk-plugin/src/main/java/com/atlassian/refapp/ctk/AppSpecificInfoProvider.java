package com.atlassian.refapp.ctk;

import javax.ws.rs.core.UriBuilder;

/**
 * Implementations of this provide application-specific information for test purpose.
 */
public interface AppSpecificInfoProvider
{
    String getAdminUsername();
    String getAdminPassword();
    String getBaseContext();
    String getPort();
    Integer getPortValue();

    UriBuilder createLocalhostUriBuilder();
}
