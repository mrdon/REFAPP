package com.atlassian.sal.refimpl.component;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.plugin.refimpl.ContainerManager;
import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.PropertyBuilder;
import org.springframework.osgi.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RefImplComponentLocator extends ComponentLocator implements BundleContextAware
{
    private BundleContext bundleContext;
    private static final Log log = LogFactory.getLog(RefImplComponentLocator.class);

    public RefImplComponentLocator()
    {

    }

    protected <T> T getComponentInternal(Class<T> iface)
    {
        log.warn("ComponentLocator should not be used for any cross-product plugins");
        try
        {
            ServiceReference[] refs = bundleContext.getServiceReferences(iface.getName(), "(" + ComponentRegistrar.HOST_COMPONENT_FLAG + "=true)");
            if (refs != null && refs.length > 0) {
                return (T) bundleContext.getService(refs[0]);
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new RuntimeException("Invalid ldap filter", e);
        }
        return null;
    }

    protected <T> T getComponentInternal(Class<T> iface, String componentKey)
    {
        log.warn("ComponentLocator should not be used for any cross-product plugins");
        try
        {
            ServiceReference[] refs = bundleContext.getServiceReferences(iface.getName(), "(&(" + ComponentRegistrar.HOST_COMPONENT_FLAG + "=true)("+ PropertyBuilder.BEAN_NAME+"))");
            if (refs != null && refs.length > 0) {
                return (T) bundleContext.getService(refs[0]);
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new RuntimeException("Invalid ldap filter", e);
        }
        return null;
    }

    protected <T> Collection<T> getComponentsInternal(Class<T> iface)
    {
        log.warn("ComponentLocator should not be used for any cross-product plugins");
        try
        {
            ServiceReference[] refs = bundleContext.getServiceReferences(iface.getName(), "(" + ComponentRegistrar.HOST_COMPONENT_FLAG + "=true)");
            List<T> objs = new ArrayList<T>();
            if (refs != null)
            {
                for (ServiceReference ref : refs) {
                    objs.add((T) bundleContext.getService(ref));
                }
            }
            return objs;
        }
        catch (InvalidSyntaxException e)
        {
            throw new RuntimeException("Invalid ldap filter", e);
        }
    }

    public void setBundleContext(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
        setComponentLocator(this);
    }
}
