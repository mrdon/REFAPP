package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.HelpPathResolver;

import org.junit.Test;

import static org.junit.Assert.assertNull;

import static org.junit.Assert.assertNotNull;

public class HelpPathResolverTest extends SpringAwareTestCase
{
    private HelpPathResolver helpPathResolver;
    
    public void setHelpPathResolver(HelpPathResolver resolver)
    {
        this.helpPathResolver = resolver;
    }
    
    @Test
    public void helpPathResolverIsProvided()
    {
        assertNotNull("A HelpPathResolver instance must be provided", helpPathResolver);
    }
    
    @Test
    public void helpPathResolverAcceptsNullKey()
    {
        helpPathResolver.getHelpPath(null);
    }
    
    @Test
    public void helpPathResolverReturnsNullForUnknownKey()
    {
        assertNull(helpPathResolver.getHelpPath("x-invalid-key"));
    }
}
