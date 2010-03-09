package com.atlassian.refapp.decorator;

import java.io.CharArrayWriter;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.templaterenderer.annotations.HtmlSafe;

public class WebResourceIncluder
{
    private final WebResourceManager webResourceManager;

    public WebResourceIncluder(WebResourceManager webResourceManager)
    {
        this.webResourceManager = webResourceManager;
    }

    @HtmlSafe
    @com.atlassian.velocity.htmlsafe.HtmlSafe
    public CharArrayWriter includeResources()
    {
        CharArrayWriter writer = new CharArrayWriter();
        webResourceManager.includeResources(writer);
        return writer;
    }
}
