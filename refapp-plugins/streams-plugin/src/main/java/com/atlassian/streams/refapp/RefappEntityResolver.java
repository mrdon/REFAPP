package com.atlassian.streams.refapp;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.EntityResolver;

import static com.atlassian.streams.api.common.Option.none;

public class RefappEntityResolver implements EntityResolver
{
    public Option<Object> apply(String key)
    {
        return none();
    }
}
