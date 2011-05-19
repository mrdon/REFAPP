package com.atlassian.refapp.sal.timezone;

import com.atlassian.sal.api.timezone.TimeZoneManager;

import java.util.TimeZone;

public class RefimplTimeZoneManager implements TimeZoneManager
{
    public TimeZone getUserTimeZone()
    {
        return getDefaultTimeZone();
    }

    public TimeZone getDefaultTimeZone()
    {
        return TimeZone.getDefault();
    }
}
