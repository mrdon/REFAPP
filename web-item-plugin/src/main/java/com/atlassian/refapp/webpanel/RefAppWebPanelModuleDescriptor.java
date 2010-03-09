package com.atlassian.refapp.webpanel;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.DefaultWebPanelModuleDescriptor;

public class RefAppWebPanelModuleDescriptor extends DefaultWebPanelModuleDescriptor
{
    public RefAppWebPanelModuleDescriptor(HostContainer hostContainer, ModuleFactory moduleClassFactory, WebInterfaceManager webInterfaceManager)
    {
        super(hostContainer, moduleClassFactory, webInterfaceManager);
    }
}
