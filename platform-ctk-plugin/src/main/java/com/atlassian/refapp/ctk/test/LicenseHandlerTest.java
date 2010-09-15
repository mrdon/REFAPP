package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.license.LicenseHandler;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LicenseHandlerTest extends SpringAwareTestCase
{
    static final String VALID_LICENSE = "AAAB+A0ODAoPeNptU9uSojAQfecrqNpnLcSZvVjF1joIte7IRRHlwaqpAI1GIYEkoMzXb7ztyux25\n" +
            "SWnO336nCSf1pCqDmLqQJdr9PQ8ev6sWsFS1TXtq5JhvoMW+jOcAOGwbEswbIlZLWxG6gQayGkJT\n" +
            "HHrIgbmZSEHxo2BJkMxKREoES4qwPg1XYzVQNQppkpCSdaXCdyAIVgNil+zZIc4TJAA40zb04a9g\n" +
            "a7cSK1TiVl7SfrDn/e2loNwbuwoF5D2Umh+IJEjzjEi/YQWSgCsATadGEWLU2WPGepouMxjEQGsZ\n" +
            "Jh3tVzmeyyWjFleA0nOdUvgApPtteou28xrOQdzaQpSvZKwOsFx3rXNvIEdLilCDkGQ7P1BZsLoM\n" +
            "e3/z9drpttaIp2+t6zUP9QUj20RwRwJTIkxvtv0R8nFHG82eZtNTcsNrDfbWzhG5bKiTINlhuNqb\n" +
            "C8mRU7CwPf5cU6qzGqSJobj3Bwz/hLFL/vZhhR7fabZeis8Pz4wexsdvpsREU+N/nqM7fcv429+M\n" +
            "G3MVR34Sx6f5AGXhaFLHUnkOi6lrBJi4Tlz3/UrlzK3aNZhGEVNw4PQWfENWa9OpxMtmzAktGC0K\n" +
            "qriHP9sBxWOyjLbkOiwfo3LKjukG/L3Uh6fnsngYsvHp3f1+LHy/hMesYtzj8BvfuQs3DAsAhQk2\n" +
            "2xkBLtW8VW42BXyyBpENLE4iQIUNrBQJqb3pmuxooZ9ycz3ikK96ns=X01nr";

    private LicenseHandler handler;

    public void setHandler(LicenseHandler handler)
    {
        this.handler = handler;
    }

    @Test
    public void testInjection()
    {
        assertTrue("License handler should be injectable", handler != null);
    }

    @Test
    public void testSetLicense()
    {
        try
		{
			handler.setLicense("somecrap");
			fail("Should throw IllegalArgumentException for invalid licenses");
		}
        catch (final IllegalArgumentException ex)
		{
			// good
		}

        try
        {
            handler.setLicense(VALID_LICENSE);
        } catch (final Exception ex)
        {
            assertTrue("Should accept a valid 2.0 license", false);
        }
    }
}
