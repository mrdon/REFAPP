package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.fileupload.CommonsFileUploadMultipartHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class RequestFactoryTest extends SpringAwareTestCase
{
    private static final int JETTY_PORT = 54241;
    private static final String MESSAGE = "Hello World!";

    private RequestFactory<Request<?, ?>> requestFactory;
    private boolean passed = false;

    private static File testFile;

    public void setRequestFactory(RequestFactory<Request<?, ?>> requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    @BeforeClass
    public static void before() throws Exception
    {
        testFile = File.createTempFile("refapp-ctk-rest-test", "tmp");
        FileUtils.writeStringToFile(testFile, "test");
    }

    @AfterClass
    public static void after() throws Exception
    {
        if (!testFile.delete())
        {
            testFile.deleteOnExit();
        }
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

    @Test
    public void testCanSendMultipartPostRequest() throws Exception
    {
        Server server = new Server(JETTY_PORT);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(SetFilesServlet.class, "/*");
        server.setHandler(handler);
        try
        {
            // start jetty.
            server.start();

            // now, make a request.
            Request<?, ?> request = requestFactory.createRequest(Request.MethodType.POST, "http://localhost:" + JETTY_PORT);
            request.setFiles(Collections.singletonList(new RequestFilePart(testFile, "testFile")));
            request.execute(new ResponseHandler()
            {
                public void handle(final Response response) throws ResponseException
                {
                    assertTrue(response.isSuccessful());
                }
            });
        }
        finally
        {
            server.stop();
        }
    }

    @Test
    public void testCanSendMultipartPutRequest() throws Exception
    {
        Server server = new Server(JETTY_PORT);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(SetFilesServlet.class, "/*");
        server.setHandler(handler);
        try
        {
            // start jetty.
            server.start();

            // now, make a request.
            Request<?, ?> request = requestFactory.createRequest(Request.MethodType.PUT, "http://localhost:" + JETTY_PORT);
            request.setFiles(Collections.singletonList(new RequestFilePart(testFile, "testFile")));
            request.execute(new ResponseHandler()
            {
                public void handle(final Response response) throws ResponseException
                {
                    assertTrue(response.isSuccessful());
                }
            });
        }
        finally
        {
            server.stop();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSetFiles() throws Exception
    {
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.PUT, "http://localhost");
        request.setFiles(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFilesWithGetRequest() throws Exception
    {
        final List<RequestFilePart> fileParts = Collections.singletonList(new RequestFilePart(testFile, "testFile"));
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.GET, "http://localhost");
        request.setFiles(fileParts);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFilesWithHeadRequest() throws Exception
    {
        final List<RequestFilePart> fileParts = Collections.singletonList(new RequestFilePart(testFile, "testFile"));
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.HEAD, "http://localhost");
        request.setFiles(fileParts);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFilesWithDeleteRequest() throws Exception
    {
        final List<RequestFilePart> fileParts = Collections.singletonList(new RequestFilePart(testFile, "testFile"));
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.DELETE, "http://localhost");
        request.setFiles(fileParts);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetFilesAfterSetRequestBody() throws Exception
    {
        final List<RequestFilePart> fileParts = Collections.singletonList(new RequestFilePart(testFile, "testFile"));
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.POST, "http://localhost");
        request.setRequestBody("body");
        request.setFiles(fileParts);
    }

    public void testSetFilesReturnsItself() throws Exception
    {
        final List<RequestFilePart> fileParts = Collections.singletonList(new RequestFilePart(testFile, "testFile"));
        final Request<?, ?> request = requestFactory.createRequest(Request.MethodType.POST, "http://localhost");
        assertEquals(request, request.setFiles(fileParts));
    }

    public static class SetFilesServlet extends HttpServlet
    {
        public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
        {
            handleMultipart(req, res);
        }

        public void doPut(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
        {
            handleMultipart(req, res);
        }

        private void handleMultipart(HttpServletRequest req, HttpServletResponse res) throws IOException
        {
            final CommonsFileUploadMultipartHandler handler = new CommonsFileUploadMultipartHandler(1000, 1000);
            final FilePart filePart = handler.getFilePart(req, "testFile");
            if (!IOUtils.toString(filePart.getInputStream()).equals("test"))
            {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
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