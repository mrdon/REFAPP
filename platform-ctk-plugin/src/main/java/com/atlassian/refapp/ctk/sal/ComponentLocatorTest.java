package com.atlassian.refapp.ctk.sal;

import java.util.Collection;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.component.ComponentLocator;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ComponentLocatorTest extends SpringAwareTestCase
{
    @Test
    public void testAccessibility()
    {
        try
        {
            final PluginController mgr = ComponentLocator.getComponent(PluginController.class);
            assertTrue("PluginController accessible in ComponentLocator", mgr != null);

            final Collection<PluginController> c = ComponentLocator.getComponents(PluginController.class);
            assertTrue("Should be at least one PluginController found", c != null && !c.isEmpty());
            assertTrue("There should be only one PluginController", c.size()==1);

            final PluginAccessor accessor = ComponentLocator.getComponent(PluginAccessor.class);
            assertTrue("PluginAccessor accessible in ComponentLocator", accessor != null);

            final Collection<PluginAccessor> ca = ComponentLocator.getComponents(PluginAccessor.class);
            assertTrue("Should be at least one PluginAccessor found", ca != null && !ca.isEmpty());
            assertTrue("There should be only one PluginAccessor", ca.size()==1);

            assertTrue("PluginAccessor should be accessible in ComponentLocator", ComponentLocator.getComponent(PluginAccessor.class) != null);
        }
        catch (final UnsupportedOperationException ex)
        {
            fail("ComponentLocator operations should be supported");
            ex.printStackTrace();
        }
    }
}
