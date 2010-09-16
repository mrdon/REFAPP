package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.auth.AuthenticationController;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class AuthenticationControllerTest extends SpringAwareTestCase
{
    private AuthenticationController controller;

    public void setController(AuthenticationController controller)
    {
        this.controller = controller;
    }

    @Test
    public void testInjection()
    {
        assertTrue("AuthenticationController should be injectable", controller != null);
    }

    @Test
    public void testShouldAttemptAuthentication()
    {
        assertTrue("should return true if not authenticated", controller.shouldAttemptAuthentication(createMockUnauthicatedRequest()));
        assertFalse("should return false if already authenticated", controller.shouldAttemptAuthentication(createMockAuthicatedRequest("hoho")));
    }

    private HttpServletRequest createMockRequest()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getScheme()).thenReturn("http");
        Mockito.when(request.getServerName()).thenReturn("example.com");
        Mockito.when(request.getServerPort()).thenReturn(8080);
        Mockito.when(request.getContextPath()).thenReturn("/context");
        return request;
    }

    private HttpServletRequest createMockUnauthicatedRequest()
    {
        HttpServletRequest request = createMockRequest();
        Mockito.when(request.getUserPrincipal()).thenReturn(null);
        return request;
    }

    private HttpServletRequest createMockAuthicatedRequest(String authenticatedUserName)
    {
        HttpServletRequest request = createMockRequest();
        Mockito.when(request.getUserPrincipal()).thenReturn(new DummyPrincipal(authenticatedUserName));
        return request;
    }

    private static class DummyPrincipal implements Principal
    {
        private String name;

        private DummyPrincipal(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}
