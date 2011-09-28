package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;

import org.junit.Test;

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
    public void helpPathKeyAndUrlNeverNull()
    {
        HelpPath path = helpPathResolver.getHelpPath("blahblahblahblah_a_random_key");
        // we can verify this only if the path here isn't null
        if (path != null)
        {
            assertNotNull("HelpPath can never have null key", path.getKey());
            assertNotNull("HelpPath can never have null url", path.getUrl());
        }
    }
}
