package com.atlassian.refapp.sal.message;

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

        if (lang != null && country != null && variant != null)
        {
            return new Locale(lang, country, variant);
        }
        else if (lang != null && country != null)
        {
            return new Locale(lang, country);
        }
        else if (lang != null)
        {
            return new Locale(lang);
        }
        else if (request.getLocale() != null)
        {
            return request.getLocale();
        }

        return Locale.getDefault();
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
