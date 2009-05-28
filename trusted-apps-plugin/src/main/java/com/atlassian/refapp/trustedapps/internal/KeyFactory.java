package com.atlassian.refapp.trustedapps.internal;

import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;

/**
 * Factory for getting key pairs to use in trusted applications.  Stores keys in the pluginsettings for possible
 * persistence between restarts.
 */
public class KeyFactory
{
    private static final String PRIVATE_KEY = "trustedapps.private-key";
    private static final String PUBLIC_KEY = "trustedapps.public-key";

    private EncryptionProvider encryptionProvider;
    private PluginSettings pluginSettings;

    public KeyFactory(EncryptionProvider encryptionProvider, PluginSettingsFactory pluginSettingsFactory)
    {
        this.encryptionProvider = encryptionProvider;
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    public KeyPair getKeyPair()
    {
        KeyPair keyPair;
        if (pluginSettings.get(PRIVATE_KEY) != null)
        {
            keyPair = fetchKeyPair();
        }
        else
        {
            keyPair = createKeyPair();
            storeKeyPair(keyPair);
        }
        return keyPair;
    }

    private KeyPair createKeyPair()
    {
        try
        {
            return encryptionProvider.generateNewKeyPair();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException("No such algorithm", e);
        }
        catch (NoSuchProviderException e)
        {
            throw new IllegalArgumentException("No such provider", e);
        }
    }

    private void storeKeyPair(KeyPair keyPair)
    {
        pluginSettings.put(PUBLIC_KEY, KeyUtils.encode(keyPair.getPublic()));
        pluginSettings.put(PRIVATE_KEY, KeyUtils.encode(keyPair.getPrivate()));
    }

    private KeyPair fetchKeyPair()
    {
        return new KeyPair(fetchPublicKey(), fetchPrivateKey());
    }

    private PrivateKey fetchPrivateKey()
    {
        String keyStr = (String) pluginSettings.get(PRIVATE_KEY);
        return KeyUtils.decodePrivateKey(encryptionProvider, keyStr);
    }

    private PublicKey fetchPublicKey()
    {
        String keyStr = (String) pluginSettings.get(PUBLIC_KEY);
        return KeyUtils.decodePublicKey(encryptionProvider, keyStr);
    }

}
