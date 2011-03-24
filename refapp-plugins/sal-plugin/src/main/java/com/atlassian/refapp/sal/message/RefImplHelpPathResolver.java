package com.atlassian.refapp.sal.message;

import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;

public class RefImplHelpPathResolver implements HelpPathResolver
{
    /**
     * RefApp doesn't have any specific help available for any plugins.
     */
    public HelpPath getHelpPath(String key)
    {
        return null;
    }
}
