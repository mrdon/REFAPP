package com.atlassian.sal.refimpl;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for extracting the base URL (scheme, hostname, port, and context path) from an {@link
 * HttpServletRequest}.
 *
 * @since 2.0.8
 */
final class HttpServletRequestBaseUrlExtractor
{
    private HttpServletRequestBaseUrlExtractor()
    {
        throw new AssertionError("may not be constructed");
    }

    /**
     * Extract the base URL from the specified request.
     *
     * @param request the current {@code HttpServletRequest}.  Must not be {@code null} or a {@code
     *                NullPointerException} will be thrown.
     * @return a string representation of the base URL of the current request
     * @throws NullPointerException if {@code request} is {@code null} 
     */
    static String extractBaseUrl(HttpServletRequest request)
    {
        if (request == null)
        {
            throw new NullPointerException("request parameter must not be null");
        }
        StringBuilder baseUrl = new StringBuilder(request.getScheme());
        baseUrl.append("://").append(request.getServerName());
        if (!isDefaultPort(request.getScheme(), request.getServerPort()))
        {
            baseUrl.append(":").append(request.getServerPort());
        }
        baseUrl.append(request.getContextPath());
        return baseUrl.toString();
    }

    private static boolean isDefaultPort(String scheme, int serverPort)
    {
        return (scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443);
    }
}
