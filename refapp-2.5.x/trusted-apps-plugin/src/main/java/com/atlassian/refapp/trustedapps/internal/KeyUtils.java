package com.atlassian.refapp.trustedapps.internal;

import com.atlassian.user.util.Base64Encoder;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import org.apache.log4j.Logger;

public class KeyUtils
{
    private static final Logger log = Logger.getLogger(KeyUtils.class);
    public static String encode(Key key)
    {
        return new String(Base64Encoder.encode(key.getEncoded()));
    }

    public static PrivateKey decodePrivateKey(EncryptionProvider encryptionProvider, String keyStr)
    {
        final byte[] data = Base64Encoder.decode(keyStr.getBytes());
        try
        {
            return encryptionProvider.toPrivateKey(data);
        }
        catch (NoSuchProviderException e)
        {
            log.error(e);
            return new InvalidPrivateKey(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error(e);
            return new InvalidPrivateKey(e);
        }
        catch (InvalidKeySpecException e)
        {
            log.error(e);
            return new InvalidPrivateKey(e);
        }
    }

    public static PublicKey decodePublicKey(EncryptionProvider encryptionProvider, String keyStr)
    {
        final byte[] data = Base64Encoder.decode(keyStr.getBytes());
        try
        {
            return encryptionProvider.toPublicKey(data);
        }
        catch (NoSuchProviderException e)
        {
            log.error(e);
            return new InvalidPublicKey(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error(e);
            return new InvalidPublicKey(e);
        }
        catch (InvalidKeySpecException e)
        {
            log.error(e);
            return new InvalidPublicKey(e);
        }
    }


    /**
     * If there are problems creating a key, one of these will be returned instead.
     * Rather than returning the actual key, the toString() will return the causal exception.
     */
    public static class InvalidPrivateKey extends InvalidKey implements PrivateKey
    {
        public InvalidPrivateKey(Exception cause)
        {
            super(cause);
        }
    }

    /**
     * If there are problems creating a key, one of these will be returned instead.
     * Rather than returning the actual key, the toString() will return the causal exception.
     */
    public static class InvalidPublicKey extends InvalidKey implements PublicKey
    {
        public InvalidPublicKey(Exception cause)
        {
            super(cause);
        }
    }

    static class InvalidKey implements Key
    {
        private final Exception cause;

        public InvalidKey(Exception cause)
        {
            this.cause = cause;
        }

        public String getAlgorithm()
        {
            return "";
        }

        ///CLOVER:OFF
        public String getFormat()
        {
            return "";
        }
        ///CLOVER:ON

        public byte[] getEncoded()
        {
            return new byte[0];
        }

        public String toString()
        {
            return "Invalid Key: " + cause.toString();
        }

        public Exception getCause()
        {
            return cause;
        }
    }


}
