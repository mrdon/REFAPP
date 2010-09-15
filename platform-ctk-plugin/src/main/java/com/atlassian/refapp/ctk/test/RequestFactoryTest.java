package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class RequestFactoryTest extends SpringAwareTestCase
{
    private RequestFactory<Request<?, ?>> requestFactory;
    private boolean passed = false;

    public void setRequestFactory(RequestFactory<Request<?, ?>> requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    @Test
    public void testInjection()
    {
        assertTrue("RequestFactory must be injectable", requestFactory != null);
    }

    @Test
    public void testRequestExecution() throws ResponseException
    {
        Request<?, ?> request = requestFactory.createRequest(Request.MethodType.GET, "http://google.com");

        request.execute(new ResponseHandler()
        {
            public void handle(final Response response) throws ResponseException
            {
                passed = response.getResponseBodyAsString().contains("Google");
            }
        });
        assertTrue("Should be able to call http://google.com and get result that contains 'google'", passed);

        request = requestFactory.createRequest(Request.MethodType.GET, "http://demo.jira.com");
        request.addSeraphAuthentication("admin", "admin");

        request.execute(new ResponseHandler()
        {
            public void handle(final Response response) throws ResponseException
            {
                passed = response.getResponseBodyAsString().contains("Joe Administrator");
            }
        });
        assertTrue("Should be able to call http://demo.jira.com and log in using seraph authentication", passed);
    }
}