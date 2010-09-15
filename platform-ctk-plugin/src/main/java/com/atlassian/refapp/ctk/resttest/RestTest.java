package com.atlassian.refapp.ctk.resttest;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.AppSpecificInfoProvider;

import com.atlassian.refapp.ctk.DummyRestResource;
import org.junit.Test;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.client.Client;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class RestTest extends SpringAwareTestCase
{
    private AppSpecificInfoProvider infoProvider;

    public void setInfoProvider(AppSpecificInfoProvider infoProvider)
    {
        this.infoProvider = infoProvider;
    }

    @Test
    public void testWithDummyRestNoParam()
    {
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(infoProvider.getPort())).path(infoProvider.getBaseContext())
                    .path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = Client.create().resource(uri).get(DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello world!", result.getMessage());
    }

    @Test
    public void testWithDummyRestWithParam()
    {
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(infoProvider.getPort())).path(infoProvider.getBaseContext())
                    .path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = Client.create().resource(uri).queryParam("who", "atlassian").get(DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello atlassian!", result.getMessage());
    }
}
