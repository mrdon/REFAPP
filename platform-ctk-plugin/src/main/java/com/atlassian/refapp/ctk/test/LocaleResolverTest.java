package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.LocaleResolver;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class LocaleResolverTest extends SpringAwareTestCase
{
    private LocaleResolver localeResolver;

    public void setLocaleResolver(LocaleResolver localeResolver)
    {
        this.localeResolver = localeResolver;
    }

    @Test
    public void testInjection()
    {
        assertTrue("LocaleResolver should be injectable", localeResolver != null);
    }

    @Test
    public void testMinimumSupportedLocales()
    {
        final Set<Locale> localeSet = localeResolver.getSupportedLocales();
        assertTrue("LocaleResolver should return at least one supported locale", localeSet.size() >= 1);
    }
}
