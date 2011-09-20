package it.com.atlassian.refapp.ctk;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RefappStreamsTest
{
    private static final String PORT = System.getProperty("http.port", "5990");
    private static final String CONTEXT = System.getProperty("context.path", "/refapp");

    @Test
    public void refappStreamsIsPresent()
    {
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(PORT)).path(CONTEXT)
            .path("plugins/servlet/streams").build();
        String results = Client.create().resource(uri).get(String.class);
        assertTrue(results.contains("admin"));
        assertTrue(results.contains("did something"));
        assertTrue(results.contains("finished another thing"));
    }
}
