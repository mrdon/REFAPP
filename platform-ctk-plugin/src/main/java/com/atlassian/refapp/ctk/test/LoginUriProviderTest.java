package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.auth.LoginUriProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class LoginUriProviderTest extends SpringAwareTestCase
{
    private LoginUriProvider provider;

    public void setProvider(LoginUriProvider provider)
    {
        this.provider = provider;
    }

    @Test
    public void testInjection()
    {
        assertTrue("LoginUriProvider should be injectable", provider != null);
    }

    @Test
    public void testGetLoginUri() throws URISyntaxException, UnsupportedEncodingException
    {
        String destUri = "http://server/dest.html?param=value";
        URI loginUri = provider.getLoginUri(new URI(destUri));
        assertTrue("Login URI should contain destination: " + loginUri.toString(), loginUri.toString().contains(URLEncoder.encode(destUri, "UTF-8")));
    }
}
