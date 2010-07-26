package com.atlassian.refapp.trustedapps.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.EscapeTool;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

public class TrustedAppsClientServlet extends HttpServlet
{
   private final VelocityEngine velocity;
   private final RequestFactory<?> requestFactory;

   public TrustedAppsClientServlet(RequestFactory<?> requestFactory) throws Exception
    {
        this.requestFactory = requestFactory;
        
        velocity = new VelocityEngine();
        velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.init();
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isBlank(request.getParameter("url")))
        {
            try
            {
                params.putAll(makeRequest(request.getParameter("url")));
            }
            catch (ResponseException e)
            {
                throw new ServletException(e);
            }
        }
        render("/client.vm", params, request, response);
    }

    private Map<String, Object> makeRequest(String url) throws ResponseException
    {
        Request request = requestFactory.createRequest(MethodType.GET, url);
        request.addTrustedTokenAuthentication();
        final Map<String, Object> responseData = new HashMap<String, Object>();
        request.execute(new ResponseHandler()
        {
            public void handle(Response response) throws ResponseException
            {
                responseData.put("responseStatusCode", response.getStatusCode());
                responseData.put("responseStatusText", response.getStatusText());
                try
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line);
                    }
                    responseData.put("responseBody", builder.toString());
                } catch (IOException e)
                {
                    throw new ResponseException(e);
                }
            }
        });
        return responseData;
    }

    private Template getTemplate(String templateName) throws ServletException
    {
        try
        {
            return velocity.getTemplate(templateName);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    private void render(String templateName, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Template template = getTemplate(templateName);
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> param : params.entrySet())
        {
            context.put(param.getKey(), param.getValue());
        }
        context.put("esc", new EscapeTool());
        context.put("request", request);
        template.merge(context, response.getWriter());
    }
}
