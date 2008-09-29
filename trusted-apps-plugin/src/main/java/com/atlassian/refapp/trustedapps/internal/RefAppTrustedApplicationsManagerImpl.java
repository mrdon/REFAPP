package com.atlassian.refapp.trustedapps.internal;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultCurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultIPMatcher;
import com.atlassian.security.auth.trustedapps.DefaultTrustedApplication;
import com.atlassian.security.auth.trustedapps.DefaultURLMatcher;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;

public class RefAppTrustedApplicationsManagerImpl implements TrustedApplicationsManager
{
    private final EncryptionProvider encryptionProvider;
    private final CurrentApplication currentApplication;
    private final Map<String, TrustedApplication> trustedApplications;

    public RefAppTrustedApplicationsManagerImpl(EncryptionProvider encryptionProvider)
    {
        this.encryptionProvider = encryptionProvider;
        
        KeyPair keyPair;
        try
        {
            keyPair = encryptionProvider.generateNewKeyPair();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException("No such algorithm", e);
        }
        catch (NoSuchProviderException e)
        {
            throw new IllegalArgumentException("No such provider", e);
        }
        
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

    public synchronized TrustedApplication addTrustedApplication(Application app, String name, long certificateTimeout, Set<String> urlPatterns, Set<String> ipPatterns)
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
