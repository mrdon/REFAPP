package com.atlassian.refapp.decorator;

import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DecoratorServletFilter implements Filter
{
    private final TemplateRenderer templateRenderer;

    public DecoratorServletFilter(TemplateRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException
    {
        if (!(servletRequest instanceof HttpServletRequest))
        {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getServletPath().equals("/index.jsp") && !request.getRequestURI().contains("index.jsp"))
        {
            // The container has sent us to the welcome page
            render("/templates/index.vm", request, response);
            return;
        }
        if (request.getServletPath().equals("/admin"))
        {
            render("/templates/admin.vm", request, response);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy()
    {
    }

    private void render(String template, HttpServletRequest request, HttpServletResponse response) throws
        IOException
    {
        response.setContentType("text/html");
        templateRenderer.render(template, getContext(request), response.getWriter());
    }

    private Map<String, Object> getContext(HttpServletRequest request)
    {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("request", request);
        return context;
    }
}
