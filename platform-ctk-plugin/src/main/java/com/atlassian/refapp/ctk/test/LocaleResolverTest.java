package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.LocaleResolver;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

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
    public void testGetLocaleNeverNull()
    {
        assertNotNull("getLocale() never returns null", localeResolver.getLocale(Mockito.mock(HttpServletRequest.class)));
    }

    @Test
    public void testGetLocaleFallbackToSystemDefaultInWorstCase()
    {
        assertEquals("in worse case it should return the system default one", Locale.getDefault(), localeResolver.getLocale(Mockito.mock(HttpServletRequest.class)));
    }

    @Test
    public void testLocaleSpecifiedInRequestTakesPrecedenceOverSystemDefault()
    {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedRequest.getLocale()).thenReturn(new Locale("ko", "kr"));
        assertEquals("locale which the client prefers should take precedence over system default", new Locale("ko", "kr"), localeResolver.getLocale(mockedRequest));
    }
}