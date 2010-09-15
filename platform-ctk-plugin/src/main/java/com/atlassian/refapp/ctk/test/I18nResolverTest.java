package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;

import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class I18nResolverTest extends SpringAwareTestCase
{
	private I18nResolver resolver;

    public void setResolver(I18nResolver resolver)
    {
        this.resolver = resolver;
    }

    @Test
    public void testInjection()
    {
        assertTrue("I18nResolver should be injectable", resolver != null);
    }

    @Test
    public void testResolve()
	{
		final Message msg = resolver.createMessage("key", "arg1");

		assertTrue("Should create valid message", msg.getArguments().length == 1 && "key".equals(msg.getKey()));
		assertTrue("Should create message collection", resolver.createMessageCollection() != null);
		assertTrue("Should return key if text can't be resolved", "some.key.that.doesnt.exist".equals(resolver.getText("some.key.that.doesnt.exist")));
        final Map<String,String> translations = resolver.getAllTranslationsForPrefix("some.key.that.doesnt.exist", Locale.US);
        assertTrue("Should return empty map of translations for unknown key", translations.keySet().isEmpty());
	}
}