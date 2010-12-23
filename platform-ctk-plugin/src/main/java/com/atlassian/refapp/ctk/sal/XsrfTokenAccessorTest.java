package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

/**
 * @since v2.9
 */
public class XsrfTokenAccessorTest extends SpringAwareTestCase
{
    private XsrfTokenAccessor xsrfTokenAccessor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    public void setXsrfTokenAccessor(final XsrfTokenAccessor xsrfTokenAccessor)
    {
        this.xsrfTokenAccessor = xsrfTokenAccessor;
    }

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testXsrfTokenAccessorAvailable()
    {
        assertNotNull("XSRF Token Accessor should be available to plugins", xsrfTokenAccessor);
    }

    @Test
    public void testGetXsrfTokenCreate()
    {
        String token = xsrfTokenAccessor.getXsrfToken(request, response, true);
        assertNotNull("XSRF token accessor should not return a null token when create is true", token);
    }

    @Test
    public void testGetXsrfTokenExisting()
    {
        String token = xsrfTokenAccessor.getXsrfToken(request, response, true);
        assertNotNull("XSRF token accessor should not return a null token when create is true", token);
        // Transfer any cookies created on the response to the request
        request.setCookies(response.getCookies());
        // Check that if we get it again, we get the same token
        assertEquals("XSRF token accessor should return the same token on successive invocations on the same request",
                token, xsrfTokenAccessor.getXsrfToken(request, response, false));
        // Test that its the same token even when we request it to create if not existing
        assertEquals("XSRF token accessor should return the same token on successive invocations on the same request",
                token, xsrfTokenAccessor.getXsrfToken(request, response, true));
    }

    @Test
    public void testGetXsrfTokenNotExisting()
    {
        assertNull("XSRF token accessor should return null when a clean request with no session or cookies is passed and create is set to false",
                xsrfTokenAccessor.getXsrfToken(request, response, false));
    }
}
