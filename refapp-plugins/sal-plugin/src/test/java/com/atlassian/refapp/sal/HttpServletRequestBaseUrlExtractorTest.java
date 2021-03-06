package com.atlassian.refapp.sal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import static com.atlassian.refapp.sal.HttpServletRequestBaseUrlExtractor.extractBaseUrl;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestBaseUrlExtractorTest
{
    @Test(expected = NullPointerException.class)
    public void throwsNullPointerExceptionIfRequestIsNull()
    {
        extractBaseUrl(null);
    }

    @Test
    public void httpUrlOnDefaultPortDoesNotIncludePort()
    {
        HttpServletRequest request = createMockRequest("http", 80);
        assertEquals("http://example.com/context", extractBaseUrl(request));
    }

    @Test
    public void httpsUrlOnDefaultPortDoesNotIncludePort()
    {
        HttpServletRequest request = createMockRequest("https", 443);
        assertEquals("https://example.com/context", extractBaseUrl(request));
    }

    @Test
    public void httpUrlOnNonDefaultPortIncludesPort()
    {
        HttpServletRequest request = createMockRequest("http", 8080);
        assertEquals("http://example.com:8080/context", extractBaseUrl(request));
    }

    @Test
    public void httpsUrlOnNonDefaultPortIncludesPort()
    {
        HttpServletRequest request = createMockRequest("https", 4433);
        assertEquals("https://example.com:4433/context", extractBaseUrl(request));
    }

    private HttpServletRequest createMockRequest(String scheme, int port)
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getScheme()).thenReturn(scheme);
        Mockito.when(request.getServerName()).thenReturn("example.com");
        Mockito.when(request.getServerPort()).thenReturn(port);
        Mockito.when(request.getContextPath()).thenReturn("/context");
        return request;
    }
}
