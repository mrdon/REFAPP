package com.atlassian.refapp.ctk;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import com.atlassian.sal.api.ApplicationProperties;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RefappStreamsTest extends AbstractRestTest
{
    private ApplicationProperties appProp;

    public void setApplicationProperties(ApplicationProperties applicationProperties)
    {
        appProp = applicationProperties;
    }

    @Test
    public void refappStreamsIsPresent() throws Exception
    {
        URI uri = UriBuilder.fromUri(appProp.getBaseUrl()).path("plugins").path("servlet").path("streams").build();
        String result = get(uri);
        assertTrue(result.contains("did something"));
        assertTrue(result.contains("finished another thing"));
    }
}