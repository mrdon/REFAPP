package com.atlassian.refapp.auth.internal;

import com.atlassian.user.repository.RepositoryIdentifier;

public class AuthPluginRepositoryIdentifier implements RepositoryIdentifier
{
    public String getKey()
    {
        return "com.atlassian.security.auth.plugin";
    }

    public String getName()
    {
        return "Refimpl Auth Plugin";
    }
}
