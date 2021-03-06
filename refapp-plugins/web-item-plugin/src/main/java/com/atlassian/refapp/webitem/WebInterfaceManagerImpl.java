package com.atlassian.refapp.webitem;

import java.util.List;
import java.util.Map;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.DefaultWebInterfaceManager;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;

/**
 * Needed to force constructor injection
 */
public class WebInterfaceManagerImpl implements WebInterfaceManager
{
    private final WebInterfaceManager webInterfaceManager;
    
    public WebInterfaceManagerImpl(PluginAccessor pluginAccessor, WebFragmentHelper webFragmentHelper)
    {
        webInterfaceManager = new DefaultWebInterfaceManager(pluginAccessor, webFragmentHelper);
    }

    public List<WebItemModuleDescriptor> getDisplayableItems(String section, Map<String, Object> context)
    {
        return webInterfaceManager.getDisplayableItems(section, context);
    }

    public List<WebSectionModuleDescriptor> getDisplayableSections(String location, Map<String, Object> context)
    {
        return webInterfaceManager.getDisplayableSections(location, context);
    }

    public List<WebItemModuleDescriptor> getItems(String section)
    {
        return webInterfaceManager.getItems(section);
    }

    public List<WebSectionModuleDescriptor> getSections(String location)
    {
        return webInterfaceManager.getSections(location);
    }

    public List<WebPanel> getDisplayableWebPanels(String s, Map<String, Object> stringObjectMap)
    {
        return webInterfaceManager.getDisplayableWebPanels(s, stringObjectMap);
    }

    public List<WebPanel> getWebPanels(String s)
    {
        return webInterfaceManager.getWebPanels(s);
    }

    public WebFragmentHelper getWebFragmentHelper()
    {
        return webInterfaceManager.getWebFragmentHelper();
    }

    public boolean hasSectionsForLocation(String location)
    {
        return webInterfaceManager.hasSectionsForLocation(location);
    }

    public List<WebPanelModuleDescriptor> getWebPanelDescriptors(String location)
    {
        return webInterfaceManager.getWebPanelDescriptors(location);
    }

    public List<WebPanelModuleDescriptor> getDisplayableWebPanelDescriptors(String location, Map<String, Object> context)
    {
        return webInterfaceManager.getDisplayableWebPanelDescriptors(location, context);
    }

    public void refresh()
    {
        webInterfaceManager.refresh();
    }
}
