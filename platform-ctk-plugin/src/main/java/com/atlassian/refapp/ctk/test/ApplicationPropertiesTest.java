package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.ApplicationProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class ApplicationPropertiesTest extends SpringAwareTestCase
{
    private ApplicationProperties appProp;

    public void setApplicationProperties(ApplicationProperties applicationProperties)
    {
        appProp = applicationProperties;
    }

    @Test
    public void testInjection()
    {
        assertTrue("ApplicationProperties should be injectable", appProp != null);
    }

    @Test
    public void testGetManyThings()
    {
        assertNotNull(appProp.getBuildDate());
        assertNotNull(appProp.getBuildNumber());
        assertNotNull(appProp.getDisplayName());
        assertNotNull(appProp.getVersion());
    }

    @Test
    public void testGetBaseUrl() throws IOException
    {
        assertNotNull(appProp.getBaseUrl());
        HttpClient client = new HttpClient();
        HttpMethod get = new GetMethod(appProp.getBaseUrl());
        assertEquals("the base url must be accessible", 200, client.executeMethod(get));
    }

    @Test
    public void testGetHomeDirectory()
    {
        assertNotNull(appProp.getHomeDirectory());
        assertTrue("the home directory must exist", appProp.getHomeDirectory().exists());
        assertTrue("it must be a directory", appProp.getHomeDirectory().isDirectory());
    }
}