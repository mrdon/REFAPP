package com.atlassian.refapp.ctk;

/**
 * Implementations of this provide application-specific information for test purpose.
 */
public interface AppSpecificInfoProvider
{
    String getAdminUsername();
    String getAdminPassword();
    String getBaseContext();
    String getPort();
}
