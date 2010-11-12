package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.auth.LoginUriProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class LoginUriProviderTest extends SpringAwareTestCase
{
    private LoginUriProvider provider;

    public void setProvider(LoginUriProvider provider)
    {
        this.provider = provider;
    }

    @Test
    public void testLoginUriProviderAvailable()
    {
        assertNotNull("LoginUriProvider should be available to plugins", provider);
    }

    @Test
    public void testGetLoginUriMustContainDestinationUri() throws URISyntaxException, UnsupportedEncodingException
    {
        String destUri = "http://server/dest.html?param=value";
        URI loginUri = provider.getLoginUri(new URI(destUri));
        assertTrue("Login URI should contain destination: " + loginUri.toString(), loginUri.toString().contains(URLEncoder.encode(destUri, "UTF-8")));
    }
}
