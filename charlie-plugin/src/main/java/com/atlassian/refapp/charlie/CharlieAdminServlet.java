package com.atlassian.refapp.charlie;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

public class CharlieAdminServlet extends HttpServlet
{
    private static final String CHARLIE_KEYS = "charlie.keys";
    private static final String CHARLIE_NAME = "charlie.name";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final TemplateRenderer templateRenderer;

    public CharlieAdminServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.templateRenderer = templateRenderer;
    }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        Map<String, Object> context = new HashMap<String, Object>();
        String delete = request.getParameter("delete");
        if (delete == null)
        {
            Map<String, String> charlies = new HashMap<String, String>();
            for (String charlie : getCharlies())
            {
                charlies.put(charlie, getCharlieName(charlie));
            }
            context.put("charlies", charlies);
            render("/templates/charliesadmin.vm", context, response);
        }
        else
        {
            List<String> charlies = getCharlies();
            charlies.remove(delete);
            storeCharlies(charlies);
            response.sendRedirect(request.getRequestURL().toString());
        }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String key = request.getParameter("key");
        String name = request.getParameter("name");
        List<String> charlies = getCharlies();
        charlies.add(key);
        storeCharlies(charlies);
        setCharlieName(key, name);
        response.sendRedirect(request.getRequestURL().toString());
    }

    private void render(String template, Map<String, Object> context, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        templateRenderer.render(template, context, response.getWriter());
    }

    private List<String> getCharlies()
    {
        List<String> charlies = (List<String>) pluginSettingsFactory.createGlobalSettings().get(CHARLIE_KEYS);
        if (charlies == null)
        {
            charlies = new ArrayList<String>();
        }
        return charlies;
    }

    private void storeCharlies(List<String> charlies)
    {
        pluginSettingsFactory.createGlobalSettings().put(CHARLIE_KEYS, charlies);
    }

    private String getCharlieName(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(key).get(CHARLIE_NAME);
    }

    private void setCharlieName(String key, String name)
    {
        pluginSettingsFactory.createSettingsForKey(key).put(CHARLIE_NAME, name);
    }

}
