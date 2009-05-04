package com.atlassian.refapp.decorator;

import com.atlassian.plugin.webresource.WebResourceManager;

import javax.servlet.http.HttpServletRequest;
import java.io.CharArrayWriter;
import java.util.Map;

public class WebResourceIncluder
{
    private final WebResourceManager webResourceManager;

    public WebResourceIncluder(WebResourceManager webResourceManager)
    {
        this.webResourceManager = webResourceManager;
    }

    public CharArrayWriter includeResources()
    {
        CharArrayWriter writer = new CharArrayWriter();
        webResourceManager.includeResources(writer);
        return writer;
    }
}
