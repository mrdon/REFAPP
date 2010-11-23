package com.atlassian.refapp.ctk;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestVersionStringComparator
{
    @Test
    public void testNewerVersionMustReturnTrue()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.3", "1.2.4"));
    }

    @Test
    public void testOlderVersionMustReturnFalse()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.4", "1.2.3"));
    }

    @Test
    public void testSameVersionMustReturnTrue()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4", "1.2.4"));
    }

    @Test
    public void testAlphaVersionIsOlder()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.3.alpha1", "1.2.3"));
    }

    @Test
    public void testAlphaVersionIsNotNewer()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.3", "1.2.3.alpha1"));
    }

    @Test
    public void testMileStoneVersionIsOlder()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.3.m5", "1.2.3"));
    }

    @Test
    public void testMileStoneVersionIsNotNewer()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.3", "1.2.3.m5"));
    }

    @Test
    public void testAlpha2NewerThanAlpha1()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.alpha1", "1.2.4.alpha2"));
    }

    @Test
    public void testAlpha1NotNewerThanAlpha2()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.4.alpha2", "1.2.4.alpha1"));
    }

    @Test
    public void testAlphaOlderThanBeta()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.alpha1", "1.2.4.beta1"));
    }

    @Test
    public void testBetaNewerThanAlpha()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.4.beta1", "1.2.4.alpha1"));
    }

    @Test
    public void testBeta11NewerThanBeta9()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.beta9", "1.2.4.beta11"));
    }

    @Test
    public void testBeta9NotNewerThanBeta11()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.4.beta11", "1.2.4.beta9"));
    }

    @Test
    public void testSnapshotNewerThanAlpha()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.alpha10", "1.2.4.SNAPSHOT"));
    }

    @Test
    public void testSnapshotNewerThanBeta()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.beta10", "1.2.4.SNAPSHOT"));
    }

    @Test
    public void testSnapshotNewerThanMileStone()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.m3", "1.2.4.SNAPSHOT"));
    }

    @Test
    public void testSnapshotNotNewerThanRelease()
    {
        assertFalse(VersionStringComparator.isSameOrNewerVersion("1.2.4", "1.2.4.SNAPSHOT"));
    }

    @Test
    public void testModifiersGetComparedByAlphaCorrectly()
    {
        assertTrue(VersionStringComparator.isSameOrNewerVersion("1.2.4.a", "1.2.4.b"));
    }
}
