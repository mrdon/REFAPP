package com.atlassian.refapp.charlie;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for the Charlie plugin pages.
 *
 * @since   v2.5.0
 */
public class CharlieServlet extends HttpServlet
{
    public static final String CHARLIE_KEYS = "charlie.keys";
    public static final String CHARLIE_NAME = "charlie.name";
    protected final PluginSettingsFactory pluginSettingsFactory;
    protected final TemplateRenderer templateRenderer;
    protected final WebInterfaceManager webInterfaceManager;

    public CharlieServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer, WebInterfaceManager webInterfaceManager)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.templateRenderer = templateRenderer;
        this.webInterfaceManager = webInterfaceManager;
    }

    protected List<String> getCharlies()
    {
        List<String> charlies = (List<String>) pluginSettingsFactory.createGlobalSettings().get(CHARLIE_KEYS);
        if (charlies == null)
        {
            charlies = new ArrayList<String>();
        }
        return charlies;
    }

    protected void storeCharlies(List<String> charlies)
    {
        pluginSettingsFactory.createGlobalSettings().put(CHARLIE_KEYS, charlies);
    }

    protected String getCharlieName(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(key).get(CHARLIE_NAME);
    }

    protected void setCharlieName(String key, String name)
    {
        pluginSettingsFactory.createSettingsForKey(key).put(CHARLIE_NAME, name);
    }

    protected void render(String template, Map<String, Object> context, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        templateRenderer.render(template, context, response.getWriter());
    }
}
