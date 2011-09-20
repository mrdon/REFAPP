package com.atlassian.streams.refapp;

import java.net.URI;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.EntityIdentifier;
import com.atlassian.streams.spi.StreamsEntityAssociationProvider;

import com.google.common.collect.ImmutableList;

import static com.atlassian.streams.api.common.Option.none;

public class RefappEntityAssociationProvider implements StreamsEntityAssociationProvider
{
    public Iterable<EntityIdentifier> getEntityIdentifiers(URI uri)
    {
        return ImmutableList.of();
    }

    public Option<URI> getEntityURI(EntityIdentifier entityIdentifier)
    {
        return none();
    }

    public Option<String> getFilterKey(EntityIdentifier entityIdentifier)
    {
        return none();
    }

    public Option<Boolean> getCurrentUserViewPermission(EntityIdentifier entityIdentifier)
    {
        return none();
    }

    public Option<Boolean> getCurrentUserEditPermission(EntityIdentifier entityIdentifier)
    {
        return none();
    }
}
