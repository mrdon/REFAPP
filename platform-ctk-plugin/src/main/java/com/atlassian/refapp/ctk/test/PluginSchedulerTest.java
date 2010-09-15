package com.atlassian.refapp.ctk.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class PluginSchedulerTest extends SpringAwareTestCase
{
    private PluginScheduler scheduler;

    public void setScheduler(PluginScheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    @Test
    public void testInjection()
    {
        assertTrue("PluginScheduler should be injectable", scheduler != null);
    }

    @Test
    public void testScheduleUnschedule() throws InterruptedException
    {
        scheduler.scheduleJob("jobname", TestJob.class, new HashMap<String, Object>(), new Date(), 10000000);
        Thread.sleep(3000);

        assertTrue("Should be able to schedule job and have it called within 3 seconds", TestJob.called);

        scheduler.unscheduleJob("jobname");

        try
        {
            scheduler.unscheduleJob("jobname");
            fail("Should throw IllegalArgumentException when unscheduling unknown job");
        }
        catch (final IllegalArgumentException ex)
        {
            // good
        }
    }

    public static class TestJob implements PluginJob
    {

        public static boolean called = false;
        public void execute(final Map<String, Object> jobDataMap)
        {
            called = true;
        }
    }
}