package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.MockedLifeCycleAwareComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LifecycleAwareTest extends SpringAwareTestCase implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Test
    @Ignore
    public void testLifeCycleAwareComponentShouldBeStartedOnlyOnce()
    {
        MockedLifeCycleAwareComponent component = (MockedLifeCycleAwareComponent)applicationContext.getBean("mockedLifeCycleComponent");
        assertEquals("LifecycleAware component should be started only once", 1, component.getCalledCount());
    }
}
