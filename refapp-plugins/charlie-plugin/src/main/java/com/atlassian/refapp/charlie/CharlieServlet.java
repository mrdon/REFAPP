package com.atlassian.refapp.charlie;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Base class for the Charlie plugin pages.
 *
 * @since   v2.5.0
 */
public class CharlieServlet extends HttpServlet
{
    protected final PluginSettingsFactory pluginSettingsFactory;
    protected final TemplateRenderer templateRenderer;
    protected final WebInterfaceManager webInterfaceManager;
    protected final CharlieStore store;

    public CharlieServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer, WebInterfaceManager webInterfaceManager, CharlieStore store)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.templateRenderer = templateRenderer;
        this.webInterfaceManager = webInterfaceManager;
        this.store = store;
    }

    protected void render(String template, Map<String, Object> context, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        templateRenderer.render(template, context, response.getWriter());
    }
}
