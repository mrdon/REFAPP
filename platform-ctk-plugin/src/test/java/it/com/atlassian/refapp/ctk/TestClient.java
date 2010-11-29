package it.com.atlassian.refapp.ctk;

import java.io.File;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.atlassian.functest.rest.TestResults;

import com.sun.jersey.api.client.Client;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 *
 */
public class TestClient
{
    private static final String PORT = System.getProperty("http.port", "5990");
    private static final String CONTEXT = System.getProperty("context.path", "/refapp");

    @Test
    public void run()
    {
        File targetDir = new File("target");
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(PORT)).path(CONTEXT)
                .path("rest").path("functest").path("1.0").path("junit").path("runTests").build();
        TestResults results = Client.create().resource(uri).queryParam("outdir", targetDir.getAbsolutePath()).get(TestResults.class);
        assertNotNull(results);
        assertEquals(0, results.result); // make sure that the number of failing tests is 0
    }
}
