package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.LocaleResolver;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class LocaleResolverTest extends SpringAwareTestCase
{
    private LocaleResolver localeResolver;

    public void setLocaleResolver(LocaleResolver localeResolver)
    {
        this.localeResolver = localeResolver;
    }

    @Test
    public void testLocaleResolverAvailable()
    {
        assertNotNull("LocaleResolver should be available to plugins", localeResolver);
    }

    @Test
    public void testAtLeastOneSupportedLocales()
    {
        final Set<Locale> localeSet = localeResolver.getSupportedLocales();
        assertTrue("LocaleResolver should return at least one supported locale", localeSet.size() >= 1);
    }

    @Test
    public void testGetLocaleForHttpServletRequestNeverNull()
    {
        assertNotNull("getLocale(HttpServletRequest) never returns null", localeResolver.getLocale(Mockito.mock(HttpServletRequest.class)));
    }

    @Test
    public void testGetLocaleNeverNull()
    {
        assertNotNull("getLocale() never returns null", localeResolver.getLocale());
    }
}