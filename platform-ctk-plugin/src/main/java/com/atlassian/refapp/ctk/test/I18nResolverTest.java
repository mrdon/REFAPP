package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;

import java.util.Locale;
import java.util.Map;

import com.atlassian.sal.api.message.MessageCollection;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
    public void testCreateMessage()
    {
        final Message msg = resolver.createMessage("key", "arg1");
        assertTrue("Should create valid message", msg.getArguments().length == 1 && "key".equals(msg.getKey()));
    }

    @Test
    public void testCreateMessageCollection()
    {
        MessageCollection msgCollection = resolver.createMessageCollection();
        assertTrue("Should be able to create message collection", msgCollection != null);
        assertTrue("newly created messageCollection should be empty", msgCollection.isEmpty());

        msgCollection.addMessage(resolver.createMessage("key1", "arg1"));
        msgCollection.addMessage(resolver.createMessage("key2", "arg2"));
        assertEquals("only message we just added" , 2, msgCollection.getMessages().size());
    }

    @Test
    public void testResolve()
    {
        assertTrue("Should return the input key if the text can't be resolved", "some.key.that.doesnt.exist".equals(resolver.getText("some.key.that.doesnt.exist")));
    }

    @Test
    public void testGerAllTranslationsForPrefix()
    {
        final Map<String,String> translations = resolver.getAllTranslationsForPrefix("some.key.that.doesnt.exist", Locale.US);
        assertTrue("Should return empty map of translations for unknown key", translations.keySet().isEmpty());
    }
}