package com.atlassian.sal.refimpl.upgrade;

import java.util.Collections;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager;

public class RefImplPluginUpgradeManager extends DefaultPluginUpgradeManager
{
    public RefImplPluginUpgradeManager(final TransactionTemplate transactionTemplate,
            final PluginAccessor pluginAccessor,
            final PluginSettingsFactory pluginSettingsFactory)
    {
        super(Collections.<PluginUpgradeTask>emptyList(), transactionTemplate, pluginAccessor, pluginSettingsFactory);
    }
}
