package com.atlassian.refapp.trustedapps;

import java.util.Map;
import java.util.Set;
import java.util.Collection;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;

public interface RefAppTrustedApplicationsManager extends TrustedApplicationsManager
{
    Collection<TrustedApplication> getTrustedApplications();
    Application getApplicationCertificate(String url) throws RetrievalException;
    TrustedApplication addTrustedApplication(Application app, long certificateTimeout, Set<String> urlPatterns, Set<String> ipPatterns);
    void deleteApplication(String id);
}
