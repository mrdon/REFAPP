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
    private EncryptionProvider encryptionProvider;
    private PluginSettings pluginSettings;

    public KeyFactory(EncryptionProvider encryptionProvider, PluginSettingsFactory pluginSettingsFactory)
    {
        this.encryptionProvider = encryptionProvider;
        pluginSettings = pluginSettingsFactory.createSettingsForKey(getClass().getName());
    }

    public KeyPair getKeyPair()
    {
        KeyPair keyPair;
        if (pluginSettings.get("public-key") != null)
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
        pluginSettings.put("public-key", KeyUtils.encode(keyPair.getPublic()));
        pluginSettings.put("private-key", KeyUtils.encode(keyPair.getPrivate()));
    }

    private KeyPair fetchKeyPair()
    {
        return new KeyPair(fetchPublicKey(), fetchPrivateKey());
    }

    private PrivateKey fetchPrivateKey()
    {
        String keyStr = (String) pluginSettings.get("private-key");
        return KeyUtils.decodePrivateKey(encryptionProvider, keyStr);
    }

    private PublicKey fetchPublicKey()
    {
        String keyStr = (String) pluginSettings.get("public-key");
        return KeyUtils.decodePublicKey(encryptionProvider, keyStr);
    }

}
