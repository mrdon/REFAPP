package it.com.atlassian.plugin.refimpl;

public class TestRequiredServices extends AbstractRefappTestCase
{
    public TestRequiredServices(String name)
    {
        super(name);
    }

    public void testRequiredServicesAvailable()
    {
        beginAt("/index.jsp");
        assertTextPresent("com.atlassian.plugin.metadata.PluginMetadataManager");
        assertTextPresent("com.atlassian.event.api.EventPublisher");
        assertTextPresent("com.atlassian.plugin.event.PluginEventManager");
        assertTextPresent("com.atlassian.plugin.PluginController");
        assertTextPresent("com.atlassian.plugin.PluginAccessor");
    }
}
