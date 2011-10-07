package com.atlassian.refapp.ctk.rest.tests;

import java.net.URI;

import com.atlassian.refapp.ctk.AbstractRestTest;
import com.atlassian.refapp.ctk.rest.DummyRestResource;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.net.ResponseException;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DummyRestTest extends AbstractRestTest
{
    private ApplicationProperties appProp;

    public void setApplicationProperties(ApplicationProperties applicationProperties)
    {
        appProp = applicationProperties;
    }

    @Test
    public void testWithDummyRestNoParam() throws ResponseException
    {
        URI uri = UriBuilder.fromUri(appProp.getBaseUrl()).path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = get(uri, DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello world!", result.getMessage());
    }

    @Test
    public void testWithDummyRestNoParamBlah() throws ResponseException
    {
        URI uri = UriBuilder.fromUri(appProp.getBaseUrl()).path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        System.out.print("rawresult=" + get(uri));
    }

    @Test
    public void testWithDummyRestWithParam() throws ResponseException
    {
        URI uri = UriBuilder.fromUri(appProp.getBaseUrl()).path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").
                queryParam("who", "atlassian").build();
        DummyRestResource.Result result = get(uri, DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello atlassian!", result.getMessage());
    }
}