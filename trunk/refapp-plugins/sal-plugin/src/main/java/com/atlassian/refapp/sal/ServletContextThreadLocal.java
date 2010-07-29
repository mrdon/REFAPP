package com.atlassian.refapp.sal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextThreadLocal
{
    private static final ThreadLocal request = new ThreadLocal();
    private static final ThreadLocal response = new ThreadLocal();

    public static ServletContext getContext()
    {
        return getRequest().getSession().getServletContext();
    }

    public static HttpServletRequest getRequest()
    {
        return (HttpServletRequest) request.get();
    }

    /**
     * @deprecated since 2.16. This method is not longer a part of public API and
     * should not be used from outside of <code>com.atlassian.core.filters</code> package.
     * The visibility of this method will be changed to package private in the future;
     * no replacement provided.
     *
     * @param httpRequest
     */
    public static void setRequest(HttpServletRequest httpRequest)
    {
        request.set(httpRequest);
    }

    static void setResponse(HttpServletResponse httpResponse)
    {
    	response.set(httpResponse);
    }

    public static HttpServletResponse getResponse()
    {
    	return (HttpServletResponse) response.get();
    }
}

