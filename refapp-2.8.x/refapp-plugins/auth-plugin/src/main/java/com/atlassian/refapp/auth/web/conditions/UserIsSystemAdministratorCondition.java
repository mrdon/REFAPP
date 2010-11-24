package com.atlassian.refapp.auth.web.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.refapp.auth.internal.UserContextHelper;

import java.util.Map;

public class UserIsSystemAdministratorCondition implements Condition
{

    public void init(Map<String, String> stringStringMap) throws PluginParseException
    {
    }

    public boolean shouldDisplay(Map<String, Object> context)
    {
        UserContextHelper userHelper = (UserContextHelper) context.get("userHelper");
        return userHelper.isRemoteUserSystemAdministrator();
    }
}

