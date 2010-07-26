package com.atlassian.refapp.sal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletContextThreadLocalFilter implements Filter
{
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        try
        {
            ServletContextThreadLocal.setRequest((HttpServletRequest) request);
            ServletContextThreadLocal.setResponse((HttpServletResponse) response);

            filterChain.doFilter(request, response);
        }
        finally
        {
            ServletContextThreadLocal.setRequest(null);
            ServletContextThreadLocal.setResponse(null);
        }
    }

    public void destroy()
    {

    }
}
