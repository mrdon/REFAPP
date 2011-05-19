package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TimeZoneManagerTest extends SpringAwareTestCase
{
    private TimeZoneManager timeZoneManager;

    public void setTimeZoneManager(TimeZoneManager timeZoneManager)
    {
        this.timeZoneManager = timeZoneManager;
    }

    @Test
    public void testNonNullDefaultTimeZone()
    {
        assertNotNull(timeZoneManager.getDefaultTimeZone());
    }

    @Test
    public void testNonNullUserTimeZone()
    {
        assertNotNull(timeZoneManager.getUserTimeZone());
    }
}