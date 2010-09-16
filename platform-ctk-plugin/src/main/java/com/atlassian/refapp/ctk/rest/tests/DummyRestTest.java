package com.atlassian.refapp.ctk.rest.tests;

import java.net.URI;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.refapp.ctk.rest.DummyRestResource;

import com.sun.jersey.api.client.Client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DummyRestTest extends SpringAwareTestCase
{
    private AppSpecificInfoProvider infoProvider;

 	public void setInfoProvider(AppSpecificInfoProvider infoProvider)
 	{
 	    this.infoProvider = infoProvider;
 	}

    @Test
    public void testWithDummyRestNoParam()
    {
        URI uri = infoProvider.createLocalhostUriBuilder().path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = Client.create().resource(uri).get(DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello world!", result.getMessage());
    }

    @Test
    public void testWithDummyRestWithParam()
    {
        URI uri = infoProvider.createLocalhostUriBuilder().path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = Client.create().resource(uri).queryParam("who", "atlassian").get(DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello atlassian!", result.getMessage());
    }
}
