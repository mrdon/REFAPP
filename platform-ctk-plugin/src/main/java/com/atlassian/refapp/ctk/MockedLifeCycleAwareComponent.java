package com.atlassian.refapp.ctk;

import com.atlassian.sal.api.lifecycle.LifecycleAware;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A mocked LifecycleAware component.
 */
public class MockedLifeCycleAwareComponent implements LifecycleAware
{
    private AtomicInteger calledCount = new AtomicInteger(0);

    public void onStart()
    {
        // just to avoid false positive if it gets called more than once concurrently.
        calledCount.incrementAndGet();
    }

    public int getCalledCount()
    {
        return calledCount.get();
    }
}
