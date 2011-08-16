package it.com.atlassian.plugin.refimpl;

import com.atlassian.webdriver.refapp.page.RefappPluginIndexPage;

import java.util.Set;

public class TestRequiredServices extends AbstractRefappTestCase
{
    public void testRequiredServicesAvailable()
    {
        RefappPluginIndexPage pluginIndexPage = PRODUCT.visit(RefappPluginIndexPage.class);
        Set<String> serviceInterfaces = pluginIndexPage.getServiceInterfaces();

        assertTrue(serviceInterfaces.contains("com.atlassian.plugin.metadata.PluginMetadataManager"));
        assertTrue(serviceInterfaces.contains("com.atlassian.event.api.EventPublisher"));
        assertTrue(serviceInterfaces.contains("com.atlassian.plugin.event.PluginEventManager"));
        assertTrue(serviceInterfaces.contains("com.atlassian.plugin.PluginController"));
        assertTrue(serviceInterfaces.contains("com.atlassian.plugin.PluginAccessor"));
    }
}
