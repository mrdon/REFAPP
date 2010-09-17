package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.ApplicationProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import javax.ws.rs.core.Response;
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
    public void testApplicationPropertiesAvailable()
    {
        assertNotNull("ApplicationProperties should be available to plugins", appProp);
    }

    @Test
    public void testGetBuildDateNotNull()
    {
        assertNotNull(appProp.getBuildDate());
    }

    @Test
    public void testGetBuildNumberNotNull()
    {
        assertNotNull(appProp.getBuildNumber());
    }

    @Test
    public void testGetDisplayNameNotNull()
    {
        assertNotNull(appProp.getDisplayName());
    }

    @Test
    public void testGetVersionNotNull()
    {
        assertNotNull(appProp.getVersion());
    }

    @Test
    public void testGetBaseUrlNotNull()
    {
        assertNotNull(appProp.getBaseUrl());
    }

    @Test
    public void testGetBaseUrlAccessible() throws IOException
    {
        HttpClient client = new HttpClient();
        HttpMethod get = new GetMethod(appProp.getBaseUrl());
        assertEquals("the base url must be accessible", Response.Status.OK.getStatusCode(), client.executeMethod(get));
    }

    @Test
    public void testGetHomeDirectoryNotNull()
    {
        assertNotNull(appProp.getHomeDirectory());
    }

    @Test
    public void testHomeDirectoryMustExist()
    {
        assertTrue("the home directory must exist", appProp.getHomeDirectory().exists());
        assertTrue("it must be a directory", appProp.getHomeDirectory().isDirectory());
    }
}