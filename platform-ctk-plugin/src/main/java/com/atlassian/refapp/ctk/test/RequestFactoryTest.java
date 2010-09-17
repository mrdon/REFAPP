package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class RequestFactoryTest extends SpringAwareTestCase
{
    private static final int JETTY_PORT = 54241;
    private static final String MESSAGE = "Hello World!";

    private RequestFactory<Request<?, ?>> requestFactory;
    private boolean passed = false;

    public void setRequestFactory(RequestFactory<Request<?, ?>> requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    @Test
    public void testRequestFactoryAvailable()
    {
        assertNotNull("RequestFactory must be available to plugins", requestFactory);
    }

    @Test
    public void testExecuteUnauthenticatedRequest() throws Exception
    {
        Server server = new Server(JETTY_PORT);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HelloServlet.class, "/*");
        server.setHandler(handler);
        try
        {
            // start jetty.
            server.start();

            // now, make a request.
            Request<?, ?> request = requestFactory.createRequest(Request.MethodType.GET, "http://localhost:" + JETTY_PORT);
            request.execute(new ResponseHandler()
            {
                public void handle(final Response response) throws ResponseException
                {
                    passed = response.getResponseBodyAsString().contains(MESSAGE);
                }
            });
            assertTrue("Should be able to get result from http", passed);
        }
        finally
        {
            server.stop();
        }
    }

    public static class HelloServlet extends HttpServlet
    {
        public void doGet(HttpServletRequest req,
                          HttpServletResponse res)
                throws ServletException, IOException
        {
            res.setContentType("text/html;charset=utf-8");
            PrintWriter out = res.getWriter();

            out.println(MESSAGE);
            out.close();
        }
    }
}