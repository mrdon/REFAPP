package com.atlassian.refapp.ctk;

import com.atlassian.refapp.ctk.confluence.ConfluenceInfoProvider;
import com.atlassian.refapp.ctk.jira.JiraInfoProvider;
import com.atlassian.refapp.ctk.refapp.RefappInfoProvider;
import org.apache.commons.lang.StringUtils;

public final class AppSpecificInfoProviderFactory
{
    private static final String PLATFORM_CTK_TESTED_APP = "platform.ctk.testedapp";

    public static AppSpecificInfoProvider create()
    {
        String app = System.getProperty(PLATFORM_CTK_TESTED_APP);
        if (StringUtils.isEmpty(app))
        {
            throw new RuntimeException(PLATFORM_CTK_TESTED_APP + " must be set for the ctk to run");
        }

        if (app.equals("refapp"))
        {
            return new RefappInfoProvider();
        }
        else if (app.equals("confluence"))
        {
            return new ConfluenceInfoProvider();
        }
        else if (app.equals("jira"))
        {
            return new JiraInfoProvider();
        }

        throw new RuntimeException("Unknown app:" + app);
    }
}
