package com.atlassian.streams.refapp;

import com.atlassian.streams.spi.StreamsValidator;

public class RefappStreamsValidator implements StreamsValidator
{
    public boolean isValidKey(String key)
    {
        return true;
    }
}
