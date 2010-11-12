package com.atlassian.refapp.ctk.sal;

import java.util.Collection;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.component.ComponentLocator;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ComponentLocatorTest extends SpringAwareTestCase
{
    @Test
    public void testPluginControllerMustBeAccessibleByComponentLocatorGetComponent()
    {
        final PluginController mgr = ComponentLocator.getComponent(PluginController.class);
        assertNotNull("PluginController accessible in ComponentLocator", mgr);
    }

    @Test
    public void testPluginControllerMustBeAccessibleByComponentLocatorGetComponents()
    {
        final Collection<PluginController> c = ComponentLocator.getComponents(PluginController.class);
        assertTrue("Should be at least one PluginController found", c != null && !c.isEmpty());
    }

    // TODO: This is commented out because it is hard to fix this behaviour in Confluence.
//    @Test
//    public void testThereShouldBeOnlyOnePluginController()
//    {
//
//        final Collection<PluginController> c = ComponentLocator.getComponents(PluginController.class);
//        assertEquals("There should be only one PluginController", 1, c.size());
//    }

    @Test
    public void testPluginAccessorMustBeAccessibleByComponentLocatorGetComponent()
    {
        final PluginAccessor pa = ComponentLocator.getComponent(PluginAccessor.class);
        assertNotNull("Should be at least one PluginAccessor found", pa);
    }

    @Test
    public void testPluginAccessorMustBeAccessibleByComponentLocatorGetComponents()
    {
        final Collection<PluginAccessor> ca = ComponentLocator.getComponents(PluginAccessor.class);
        assertTrue("Should be at least one PluginAccessor found", ca != null && !ca.isEmpty());
    }

    @Test
    public void testThereShouldBeOnlyOnePluginAccessor()
    {
        final Collection<PluginAccessor> c = ComponentLocator.getComponents(PluginAccessor.class);
        assertEquals("There should be only one PluginAccesor", 1, c.size());
    }
}
