package com.atlassian.refapp.trustedapps.internal;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultCurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultTrustedApplication;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class RefAppTrustedApplicationsManagerImpl implements TrustedApplicationsManager, TrustedApplicationsConfigurationManager
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
            keyPair.getPrivate(), keyFactory.getApplicationId());
    }

    public CurrentApplication getCurrentApplication()
    {
        return currentApplication;
    }

    public synchronized TrustedApplication getTrustedApplication(final String id)
    {
        return load(id);
    }

    public Application getApplicationCertificate(final String url) throws RetrievalException
    {
        return encryptionProvider.getApplicationCertificate(url);
    }

    public TrustedApplication addTrustedApplication(final Application app, final RequestConditions conditions)
    {
        final TrustedApplication trustedApp = new DefaultTrustedApplication(
            encryptionProvider,
            app.getPublicKey(),
            app.getID(),
            conditions);
        store(app, conditions);
        return trustedApp;
    }

    public Collection<TrustedApplication> getTrustedApplications()
    {
        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final List<String> ids = (List<String>) pluginSettings.get(TRUSTED_APPS_KEY);
        final Collection<TrustedApplication> trustedApplications = new ArrayList<TrustedApplication>();
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

    public boolean deleteApplication(final String id)
    {
        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final List<String> ids = (List<String>) pluginSettings.get(TRUSTED_APPS_KEY);
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
        return pluginSettings.remove(TRUSTED_APP_KEY_PREFIX + id) != null;
    }

    private void store(final Application application, final RequestConditions conditions)
    {
        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final List<String> ids = (List<String>) ObjectUtils.defaultIfNull(
                pluginSettings.get(TRUSTED_APPS_KEY), new ArrayList<String>());
        if (!ids.contains(application.getID()))
        {
            ids.add(application.getID());
        }
        pluginSettings.put(TRUSTED_APPS_KEY, ids);
        Properties props = new Properties();
        props.put(PUBLIC_KEY_KEY, KeyUtils.encode(application.getPublicKey()));
        props.put(TIMEOUT_KEY, Long.toString(conditions.getCertificateTimeout()));
        props.put(URLS_KEY, iterableToString(conditions.getURLPatterns()));
        props.put(IPS_KEY, iterableToString(conditions.getIPPatterns()));
        pluginSettings.put(TRUSTED_APP_KEY_PREFIX + application.getID(), props);
    }

    private TrustedApplication load(final String id)
    {
        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final Properties props = (Properties) pluginSettings.get(TRUSTED_APP_KEY_PREFIX + id);
        if (props == null)
        {
            return null;
        }
        else
        {
            final String publicKey = props.getProperty(PUBLIC_KEY_KEY);
            final long timeout = Long.parseLong(props.getProperty(TIMEOUT_KEY));
            final String[] urls = decodeCommaSeparatedString(props.getProperty(URLS_KEY));
            final String[] ips = decodeCommaSeparatedString(props.getProperty(IPS_KEY));
            return new DefaultTrustedApplication(encryptionProvider, KeyUtils.decodePublicKey(encryptionProvider,
                publicKey), id, RequestConditions
                    .builder()
                    .setCertificateTimeout(timeout)
                    .addURLPattern(urls)
                    .addIPPattern(ips)
                    .build());
        }
    }

    private static String iterableToString(final Iterable<String> iterable)
    {
        return StringUtils.join(iterable.iterator(), ',');
    }

    private static String[] decodeCommaSeparatedString(final String str)
    {
        if (str == null || str.length() == 0)
        {
            return new String[]{};
        }
        return str.split(",");
    }

}
