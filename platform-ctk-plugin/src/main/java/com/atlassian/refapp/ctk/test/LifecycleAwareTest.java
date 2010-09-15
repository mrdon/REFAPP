package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.LifeCycleAwareStateHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class LifecycleAwareTest extends SpringAwareTestCase implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testCallCount()
    {
        LifeCycleAwareStateHolder stateHolder = (LifeCycleAwareStateHolder)applicationContext.getBean("lifeCycleStateHolder");
        assertTrue("LifecycleAware component should be called only once", stateHolder.getCalledCount() == 1);
    }
}
