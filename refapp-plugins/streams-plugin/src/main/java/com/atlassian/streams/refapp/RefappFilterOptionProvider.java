package com.atlassian.streams.refapp;

import com.atlassian.streams.spi.StreamsFilterOption;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;

import com.google.common.collect.ImmutableList;

public class RefappFilterOptionProvider implements StreamsFilterOptionProvider
{
    public Iterable<StreamsFilterOption> getFilterOptions()
    {
        return ImmutableList.of();
    }

    public Iterable<StreamsFilterOptionProvider.ActivityOption> getActivities()
    {
        return ImmutableList.of();
    }
}
