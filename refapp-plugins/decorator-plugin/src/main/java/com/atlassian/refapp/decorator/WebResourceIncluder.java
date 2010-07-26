package com.atlassian.refapp.decorator;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.templaterenderer.annotations.HtmlSafe;

import java.io.CharArrayWriter;

public class WebResourceIncluder
{
    private final WebResourceManager webResourceManager;

    public WebResourceIncluder(WebResourceManager webResourceManager)
    {
        this.webResourceManager = webResourceManager;
    }

    @HtmlSafe //this is required for backwards-compatibility with atlassian-template-renderer versions prior to 2.5.0
    @com.atlassian.velocity.htmlsafe.HtmlSafe
    public CharArrayWriter includeResources()
    {
        CharArrayWriter writer = new CharArrayWriter();
        webResourceManager.includeResources(writer);
        return writer;
    }
}
