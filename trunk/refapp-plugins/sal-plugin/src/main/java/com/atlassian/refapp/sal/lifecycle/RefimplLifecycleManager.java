package com.atlassian.refapp.sal.lifecycle;

import java.util.Collections;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.plugin.event.PluginEventManager;

public class RefimplLifecycleManager extends DefaultLifecycleManager
{
    public RefimplLifecycleManager(PluginEventManager mgr)
    {
        super(mgr);
        setLifecycleAwareListeners(Collections.<LifecycleAware>emptyList());
    }
    
    public boolean isApplicationSetUp()
    {
        return true;
    }
}
