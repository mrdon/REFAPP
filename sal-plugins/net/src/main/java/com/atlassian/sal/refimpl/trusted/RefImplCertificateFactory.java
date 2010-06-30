package com.atlassian.sal.refimpl.trusted;

import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;

public class RefImplCertificateFactory implements CertificateFactory
{
    private final TrustedApplicationsManager trustedAppsManager;

    public RefImplCertificateFactory(TrustedApplicationsManager trustedAppsManager)
    {
        this.trustedAppsManager = trustedAppsManager;
    }

    public EncryptedCertificate createCertificate(String username)
    {
        return trustedAppsManager.getCurrentApplication().encode(username);
    }
}
