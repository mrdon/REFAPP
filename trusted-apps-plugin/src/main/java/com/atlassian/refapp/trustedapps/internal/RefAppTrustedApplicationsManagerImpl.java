package com.atlassian.refapp.trustedapps.internal;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.atlassian.refapp.trustedapps.RefAppTrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultCurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultIPMatcher;
import com.atlassian.security.auth.trustedapps.DefaultTrustedApplication;
import com.atlassian.security.auth.trustedapps.DefaultURLMatcher;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;

public class RefAppTrustedApplicationsManagerImpl implements RefAppTrustedApplicationsManager
{
    private final EncryptionProvider encryptionProvider;
    private final CurrentApplication currentApplication;
    private final Map<String, TrustedApplication> trustedApplications;

    public RefAppTrustedApplicationsManagerImpl(EncryptionProvider encryptionProvider, KeyFactory keyFactory)
    {
        this.encryptionProvider = encryptionProvider;
        
        KeyPair keyPair = keyFactory.getKeyPair();
        
        currentApplication = new DefaultCurrentApplication(encryptionProvider, keyPair.getPublic(), keyPair.getPrivate(), "RefApp");
        trustedApplications = new HashMap<String, TrustedApplication>();
    }

    public CurrentApplication getCurrentApplication()
    {
        return currentApplication;
    }

    public synchronized TrustedApplication getTrustedApplication(String id)
    {
        return trustedApplications.get(id);
    }
    
    public Application getApplicationCertificate(String url) throws RetrievalException
    {
        return encryptionProvider.getApplicationCertificate(url);
    }

    public synchronized TrustedApplication addTrustedApplication(Application app, long certificateTimeout, Set<String> urlPatterns, Set<String> ipPatterns)
    {
        TrustedApplication trustedApp = new DefaultTrustedApplication(
            encryptionProvider, 
            app.getPublicKey(), 
            app.getID(), 
            certificateTimeout, 
            new DefaultURLMatcher(urlPatterns), 
            new DefaultIPMatcher(ipPatterns)
        );
        trustedApplications.put(trustedApp.getID(), trustedApp);
        return trustedApp;
    }

    public Map<String, TrustedApplication> getTrustedApplications()
    {
        return trustedApplications;
    }

    public void deleteApplication(String id)
    {
        trustedApplications.remove(id);
    }
}
