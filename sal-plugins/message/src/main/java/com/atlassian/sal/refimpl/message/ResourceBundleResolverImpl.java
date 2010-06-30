package com.atlassian.sal.refimpl.message;

import java.util.ResourceBundle;
import java.util.Locale;

class ResourceBundleResolverImpl implements ResourceBundleResolver
{
    public ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader classLoader)
    {
        return ResourceBundle.getBundle(bundleName, locale, classLoader);
    }
}
