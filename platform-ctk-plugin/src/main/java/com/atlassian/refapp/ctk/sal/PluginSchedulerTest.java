package com.atlassian.refapp.ctk.sal;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class PluginSchedulerTest extends SpringAwareTestCase
{
    private PluginScheduler scheduler;
    private SecureRandom random = new SecureRandom();

    public void setScheduler(PluginScheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    @Test
    public void testPluginSchedulerAvailable()
    {
        assertNotNull("PluginScheduler should be available to plugins", scheduler);
    }

    @Test
    public void testScheduledJobMustBeExecuted() throws InterruptedException
    {
        // reset the counter first.
        TestJob.calledCount.set(0);

        // schedule the job to increment the counter.
        scheduler.scheduleJob("job" + random.nextInt(), TestJob.class, new HashMap<String, Object>(), new Date(), 10000000);

        final long startWait = System.currentTimeMillis();

        // wait maximum 65 secs.
        // this is because in some apps such as jira, job executions can be delayed up to 60 seconds.
        while (System.currentTimeMillis() - startWait < 65000)
        {
            Thread.sleep(1000);
            if (TestJob.calledCount.get() == 1)
            {
                break;
            }
        }

        assertEquals("Should be able to schedule job and have it called only once", 1, TestJob.calledCount.get());
    }

    @Test
    public void testUnscheduleExistingJobShouldNotThrowException()
    {
        String jobName = "job" + random.nextInt();
        scheduler.scheduleJob(jobName, TestJob.class, new HashMap<String, Object>(), new Date(), 10000000);

        // this unschedule must be ok
        scheduler.unscheduleJob(jobName);
    }

    @Test
    public void testUnscheduleNonExistingJobShouldThrowIAE()
    {
        try
        {
            // this unschedule must die.
            scheduler.unscheduleJob("job" + random.nextInt());
            fail("Should throw IllegalArgumentException when unscheduling an unknown job");
        }
        catch (final IllegalArgumentException ex)
        {
            // good
        }
    }

    public static class TestJob implements PluginJob
    {
        public static AtomicInteger calledCount = new AtomicInteger();

        public void execute(final Map<String, Object> jobDataMap)
        {
            // This for avoiding ripple effect.
            calledCount.incrementAndGet();
        }
    }
}