package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TransactionTemplateTest extends SpringAwareTestCase
{
    private TransactionTemplate template;
    private boolean called = false;

    public void setTemplate(TransactionTemplate template)
    {
        this.template = template;
    }

    @Test
    public void testInjection()
    {
        assertTrue("TransactionTemplate should be injectable", template != null);
    }

    @Test
    public void textExecution()
    {
        final String result = (String) template.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                called = true;
                return "hi";
            }
        });

        assertTrue("Should have executed callback in a transaction", called);
        assertTrue("Should have returned object from callback", "hi".equals(result));
    }
}