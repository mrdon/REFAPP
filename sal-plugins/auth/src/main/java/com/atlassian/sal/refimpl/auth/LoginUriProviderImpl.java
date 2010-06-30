package com.atlassian.sal.refimpl.auth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;

public class LoginUriProviderImpl implements LoginUriProvider
{
    private final ApplicationProperties applicationProperties;

    public LoginUriProviderImpl(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }
    
    public URI getLoginUri(URI returnUri)
    {
        try
        {
            return URI.create(applicationProperties.getBaseUrl() + "/plugins/servlet/login?redir=" + URLEncoder.encode(returnUri.toString(), "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Your JVM is broken", e);
        }
    }
}
