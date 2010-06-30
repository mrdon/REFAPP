package com.atlassian.sal.refimpl.message;

import com.atlassian.sal.api.message.LocaleResolver;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class RefimplLocaleResolver implements LocaleResolver
{
    public Locale getLocale(HttpServletRequest request)
    {
        String country = request.getParameter("locale.country");
        String lang = request.getParameter("locale.lang");
        String variant = request.getParameter("locale.variant");

        Locale locale;
        if (lang != null && country != null && variant != null)
        {
            locale = new Locale(lang, country, variant);
        }
        else if (lang != null && country != null)
        {
            locale = new Locale(lang, country);
        }
        else if (lang != null)
        {
            locale = new Locale(lang);
        }
        else
        {
            locale = request.getLocale();
        }
        return locale;
    }

    public Set<Locale> getSupportedLocales()
    {
        final Set<Locale> ret = new HashSet<Locale>();
        ret.add(new Locale("en", "AU"));
        ret.add(Locale.US);
        ret.add(Locale.ENGLISH);
        ret.add(Locale.FRENCH);
        ret.add(Locale.GERMAN);
        return ret;
    }
}
