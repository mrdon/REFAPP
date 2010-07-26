package com.atlassian.refapp.webitem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.plugin.AutowireCapablePlugin;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.TemplateRendererFactory;

public class WebFragmentHelperImpl implements WebFragmentHelper
{
    private final Log logger = LogFactory.getLog(getClass());
    
    private final TemplateRenderer renderer;

    public WebFragmentHelperImpl(TemplateRendererFactory rendererFactory)
    {
        this.renderer = rendererFactory.getInstance(getClass().getClassLoader());
    }
    
    public String getI18nValue(String key, List<?> arguments, Map<String, Object> context)
    {
        if (!context.containsKey("i18n"))
        {
            logger.info("context does not contain an I18nResolver as i18n, unable to get value");
            return key;
        }
        I18nResolver i18n = (I18nResolver) context.get("i18n");
        Serializable[] params = new Serializable[0];
        if (arguments != null)
        {
            // Just hope it's Serializable...
            params = arguments.toArray(params);
        }
        return i18n.getText(key, params);
    }

    public Condition loadCondition(String className, Plugin plugin) throws ConditionLoadingException
    {
        try
        {
            if (plugin instanceof AutowireCapablePlugin)
            {
                Class conditionClass = plugin.loadClass(className, getClass());
                return (Condition) ((AutowireCapablePlugin) plugin).autowire(conditionClass);
            }
            else
            {
                throw new ConditionLoadingException("Plugin " + plugin.getKey() + " is not autowire capable, could not load condition.");
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new ConditionLoadingException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ConditionLoadingException(e);
        }
    }

    public ContextProvider loadContextProvider(String className, Plugin plugin) throws ConditionLoadingException
    {
        try
        {
            if (plugin instanceof AutowireCapablePlugin)
            {
                Class conditionClass = plugin.loadClass(className, getClass());
                return (ContextProvider) ((AutowireCapablePlugin) plugin).autowire(conditionClass);
            }
            else
            {
                throw new ConditionLoadingException("Plugin " + plugin.getKey() + " is not autowire capable, could not load context.");
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new ConditionLoadingException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ConditionLoadingException(e);
        }
    }

    public String renderVelocityFragment(String fragment, Map<String, Object> context)
    {
        return renderer.renderFragment(fragment, context);
    }
}
