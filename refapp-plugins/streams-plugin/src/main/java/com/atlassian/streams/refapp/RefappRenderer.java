package com.atlassian.streams.refapp;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsEntry.Renderer;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;

import com.google.common.base.Function;

import static com.atlassian.streams.api.common.Option.none;

public class RefappRenderer implements Renderer
{
    private final Function<StreamsEntry, Html> titleRenderer;

    public RefappRenderer(StreamsEntryRendererFactory rendererFactory)
    {
        this.titleRenderer = rendererFactory.newTitleRenderer("streams.title.action");
    }

    public Html renderTitleAsHtml(StreamsEntry streamsEntry)
    {
        return titleRenderer.apply(streamsEntry);
    }

    public Option<Html> renderSummaryAsHtml(StreamsEntry streamsEntry)
    {
        return none();
    }

    public Option<Html> renderContentAsHtml(StreamsEntry streamsEntry)
    {
        return none();
    }
}
