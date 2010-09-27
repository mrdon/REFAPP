package com.atlassian.refapp.ctk.rest.tests;

import java.net.URI;
import java.util.Map;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.refapp.ctk.rest.DummyRestResource;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DummyRestTest extends SpringAwareTestCase
{
    private AppSpecificInfoProvider infoProvider;
    private RequestFactory requestFactory;

 	public void setInfoProvider(AppSpecificInfoProvider infoProvider)
 	{
 	    this.infoProvider = infoProvider;
 	}

    @Test
    public void testWithDummyRestNoParam() throws ResponseException
    {
        URI uri = infoProvider.createLocalhostUriBuilder().path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").build();
        DummyRestResource.Result result = get(uri, DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello world!", result.getMessage());
    }

    @Test
    public void testWithDummyRestWithParam() throws ResponseException
    {
        URI uri = infoProvider.createLocalhostUriBuilder().path("rest").path("platform-ctk").path("latest").path("dummy").path("hello").
                queryParam("who", "atlassian").build();
        DummyRestResource.Result result = get(uri, DummyRestResource.Result.class);
        assertNotNull(result);
        assertEquals("hello atlassian!", result.getMessage());
    }

    public void setRequestFactory(RequestFactory requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    private <R> R get(URI uri, final Class<R> resultClass)
            throws ResponseException
    {
        Request request = requestFactory.createRequest(Request.MethodType.GET, uri.toString());
        Object result = request.executeAndReturn(
                new ReturningResponseHandler()
                {
                    public Object handle(Response response) throws ResponseException
                    {
                        return response.getEntity(resultClass);
                    }
                }
        );
        return (R) result;
    }
}
