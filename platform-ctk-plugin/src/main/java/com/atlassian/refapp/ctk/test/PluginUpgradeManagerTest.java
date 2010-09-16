package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.MockedUpgradeTask;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeManager;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static org.junit.Assert.assertTrue;

public class PluginUpgradeManagerTest extends SpringAwareTestCase implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private PluginUpgradeManager upgradeManager;
    private PluginSettingsFactory pluginSettingsFactory;

    public void setUpgradeManager(PluginUpgradeManager upgradeManager)
    {
        this.upgradeManager = upgradeManager;
    }

    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testInjection()
    {
        assertTrue("PluginUpgradeManager should be injectable", upgradeManager != null);
    }

    @Test
    public void testUpgrade()
    {
        MockedUpgradeTask upgradeTask = (MockedUpgradeTask)applicationContext.getBean("mockedUpgradeTask");

        try
        {
            if (upgradeTask.getCalledCount() == 0)
            {
                upgradeManager.upgrade();
                assertTrue("Upgrade task should have been called once since we have just called upgrade()", upgradeTask.getCalledCount() == 1);
            }

            // at this stage, the count should be one since upgrade() has been called at least once.
            // another upgrade call here should have no effect.
            upgradeManager.upgrade();
            assertTrue("Upgrade task should not have been called again", upgradeTask.getCalledCount() == 1);

            // Yes, we just happen to know how to clear the data build number....
            pluginSettingsFactory.createGlobalSettings().remove(upgradeTask.getPluginKey() + ":build");
        }
        finally
        {
            // reset the counter for the next test.
            upgradeTask.reset();
        }
    }
}
