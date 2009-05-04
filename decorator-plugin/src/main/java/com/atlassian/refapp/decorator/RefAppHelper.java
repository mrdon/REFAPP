package com.atlassian.refapp.decorator;

import org.springframework.beans.factory.FactoryBean;
import com.atlassian.sal.api.user.UserManager;

import java.util.Map;
import java.util.HashMap;

public class RefAppHelper implements FactoryBean
{
    private final UserManager userManager;

    public RefAppHelper(UserManager userManager)
    {
        this.userManager = userManager;
    }

    public Object getObject() throws Exception
    {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("user", userManager.getRemoteUsername());
        return context;
    }

    public Class getObjectType()
    {
        return Map.class;
    }

    public boolean isSingleton()
    {
        return false;
    }
}
