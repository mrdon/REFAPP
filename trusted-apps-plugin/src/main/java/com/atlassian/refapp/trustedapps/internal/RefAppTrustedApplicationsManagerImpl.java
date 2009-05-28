package com.atlassian.refapp.trustedapps.internal;

import java.security.KeyPair;
import java.util.*;

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
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class RefAppTrustedApplicationsManagerImpl implements RefAppTrustedApplicationsManager
{
    private static final String TRUSTED_APPS_KEY = "trustedapps";
    private static final String TRUSTED_APP_KEY_PREFIX = "trustedapp.";

    private static final String PUBLIC_KEY_KEY = "public.key";
    private static final String TIMEOUT_KEY = "timeout";
    private static final String URLS_KEY = "urls";
    private static final String IPS_KEY = "ips";

    private final EncryptionProvider encryptionProvider;
    private final CurrentApplication currentApplication;
    private final PluginSettingsFactory pluginSettingsFactory;

    public RefAppTrustedApplicationsManagerImpl(EncryptionProvider encryptionProvider, KeyFactory keyFactory,
        PluginSettingsFactory pluginSettingsFactory)
    {
        this.encryptionProvider = encryptionProvider;
        this.pluginSettingsFactory = pluginSettingsFactory;

        KeyPair keyPair = keyFactory.getKeyPair();

        currentApplication = new DefaultCurrentApplication(encryptionProvider, keyPair.getPublic(),
            keyPair.getPrivate(), "RefApp");
    }

    public CurrentApplication getCurrentApplication()
    {
        return currentApplication;
    }

    public synchronized TrustedApplication getTrustedApplication(String id)
    {
        return load(id);
    }

    public Application getApplicationCertificate(String url) throws RetrievalException
    {
        return encryptionProvider.getApplicationCertificate(url);
    }

    public synchronized TrustedApplication addTrustedApplication(Application app, long certificateTimeout,
        Set<String> urlPatterns, Set<String> ipPatterns)
    {
        TrustedApplication trustedApp = new DefaultTrustedApplication(
            encryptionProvider,
            app.getPublicKey(),
            app.getID(),
            certificateTimeout,
            new DefaultURLMatcher(urlPatterns),
            new DefaultIPMatcher(ipPatterns)
        );
        store(app, certificateTimeout, urlPatterns, ipPatterns);
        return trustedApp;
    }

    public Collection<TrustedApplication> getTrustedApplications()
    {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        List<String> ids = (List<String>) pluginSettings.get(TRUSTED_APPS_KEY);
        Collection<TrustedApplication> trustedApplications = new ArrayList<TrustedApplication>();
        if (ids != null)
        {
            for (String id : ids)
            {
                TrustedApplication app = load(id);
                if (app != null)
                {
                    trustedApplications.add(app);
                }
            }
        }
        return trustedApplications;
    }

    public void deleteApplication(String id)
    {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        List<String> ids = (List<String>) pluginSettings.get(TRUSTED_APPS_KEY);
        if (ids != null && ids.contains(id))
        {
            ids.remove(id);
            if (ids.isEmpty())
            {
                pluginSettings.remove(TRUSTED_APPS_KEY);
            }
            else
            {
                pluginSettings.put(TRUSTED_APPS_KEY, ids);
            }
        }
        pluginSettings.remove(TRUSTED_APP_KEY_PREFIX + id);
    }

    private void store(Application application, long certificateTimeout, Set<String> urlPatterns,
        Set<String> ipPatterns)
    {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        List<String> ids = (List<String>) pluginSettings.get(TRUSTED_APPS_KEY);
        if (ids == null)
        {
            ids = new ArrayList<String>();
        }
        if (!ids.contains(application.getID()))
        {
            ids.add(application.getID());
        }
        pluginSettings.put(TRUSTED_APPS_KEY, ids);
        Properties props = new Properties();
        props.put(PUBLIC_KEY_KEY, KeyUtils.encode(application.getPublicKey()));
        props.put(TIMEOUT_KEY, Long.toString(certificateTimeout));
        props.put(URLS_KEY, setToString(urlPatterns));
        props.put(IPS_KEY, setToString(ipPatterns));
        pluginSettings.put(TRUSTED_APP_KEY_PREFIX + application.getID(), props);
    }

    private TrustedApplication load(String id)
    {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        Properties props = (Properties) pluginSettings.get(TRUSTED_APP_KEY_PREFIX + id);
        if (props == null)
        {
            return null;
        }
        String publicKey = props.getProperty(PUBLIC_KEY_KEY);
        long timeout = Long.parseLong(props.getProperty(TIMEOUT_KEY));
        Set<String> urls = stringToSet(props.getProperty(URLS_KEY));
        Set<String> ips = stringToSet(props.getProperty(IPS_KEY));
        return new DefaultTrustedApplication(encryptionProvider, KeyUtils.decodePublicKey(encryptionProvider,
            publicKey), id, timeout, new DefaultURLMatcher(urls), new DefaultIPMatcher(ips));
    }

    private static String setToString(Set<String> set)
    {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String item : set)
        {
            sb.append(sep).append(item);
            sep = ",";
        }
        return sb.toString();
    }

    private static Set<String> stringToSet(String str)
    {
        if (str == null || str.length() == 0)
        {
            return Collections.emptySet();
        }
        return new HashSet<String>(Arrays.asList(str.split(",")));
    }

}
