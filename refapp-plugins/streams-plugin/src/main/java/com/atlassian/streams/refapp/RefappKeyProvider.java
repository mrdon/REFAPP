package com.atlassian.streams.refapp;

import com.atlassian.streams.spi.StreamsKeyProvider;

import com.google.common.collect.ImmutableList;

public class RefappKeyProvider implements StreamsKeyProvider
{
    public Iterable<StreamsKey> getKeys()
    {
        return ImmutableList.of(new StreamsKey("REFAPP", "Reference App"));
    }
}

