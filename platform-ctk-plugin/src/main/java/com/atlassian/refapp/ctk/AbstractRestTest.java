package com.atlassian.refapp.ctk;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;

import java.net.URI;

public abstract class AbstractRestTest extends SpringAwareTestCase
{
    private AppSpecificInfoProvider infoProvider;
    private RequestFactory requestFactory;

    public void setInfoProvider(AppSpecificInfoProvider infoProvider)
    {
        this.infoProvider = infoProvider;
    }

    public void setRequestFactory(RequestFactory requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    protected <R> R get(URI uri, final Class<R> resultClass)
            throws ResponseException
    {
        Request request = createRequest(uri);
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

    protected String get(URI uri) throws ResponseException
    {
        Request request = createRequest(uri);
        return request.execute();
    }

    private Request createRequest(URI uri)
    {
        Request request = requestFactory.createRequest(Request.MethodType.GET, uri.toString());
        request.addBasicAuthentication(infoProvider.getAdminUsername(), infoProvider.getAdminPassword());
        request.setRequestContentType("application/xml");
        return request;
    }
}
