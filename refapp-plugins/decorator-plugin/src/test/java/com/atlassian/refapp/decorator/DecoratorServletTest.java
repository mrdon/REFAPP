package com.atlassian.refapp.decorator;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.refapp.auth.external.WebSudoSessionManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class DecoratorServletTest
{
    @Mock TemplateRenderer templateRenderer;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock ServletConfig servletConfig;
    @Mock ServletContext servletContext;
    @Mock Page page;
    
    @Mock WebSudoSessionManager webSudoSessionManager;
    
    @Before
    public final void setUpMockBehaviour()
    {
        Mockito.when(request.getAttribute(RequestConstants.PAGE)).thenReturn(page);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
    }
    
    @Test
    public void noExceptionsAreThrownWhenPropertiesAreNotAvailable() throws ServletException, IOException
    {
        DecoratorServlet ds = new DecoratorServlet(templateRenderer, webSudoSessionManager);
        ds.init(servletConfig);
        ds.service(request, response);
    }
    
    @Test
    public void exceptionsAreNotPropagatedWhenStreamReadFails() throws ServletException, IOException
    {
        InputStream immediatelyFailingInputStream = Mockito.mock(InputStream.class);
        Mockito.when(immediatelyFailingInputStream.read()).thenThrow(new IOException());
        
        Mockito.when(servletContext.getResourceAsStream(Mockito.anyString())).thenReturn(immediatelyFailingInputStream);
        DecoratorServlet ds = new DecoratorServlet(templateRenderer, webSudoSessionManager);
        ds.init(servletConfig);
        ds.service(request, response);
    }
}
