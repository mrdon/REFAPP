package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.license.LicenseHandler;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class LicenseHandlerTest extends SpringAwareTestCase
{
    static final String VALID_LICENSE =
            "AAACUg0ODAoPeNqlVtFu2jAUfecrIu05iMA0pEqRRiFq0wFFNP0AYy6tp8RGtsPG3y9xYgiJHW/sM\n" +
            "bFzz73nHJ/4y4pRbwHYG4+8IHj4On0YTbzoLSmeg9Fgh7IdY8MlwUAFRL+PhJ8XSEK4mTwP3oCfg\n" +
            "MeL8HEbzfzJt5cn/2U03fpxMJ0OMGe/9sM5ByQJo+qbsqIfjP3xqFjNMdmlMJwzKhGWa5RBONtzg\n" +
            "qj3DNkR0sFPwpEGTs5HUFuSorN4/VRXLz4kJwglz+FacYO4pMDV9nb/e1I2E76vl/EqTqLF4EDEJ\n" +
            "5z72qiQXvkHokSoWcKZTJEQxZ4raHRCaV6tHlAqGu2sEKESKKK4TZ/GvmlYTW1BU2v2epjRwy0l5\n" +
            "QsbgzUz6zzbAX89PKrHJcMonX0AlSL0g6qAHVD1Y1W4AW76bgECc3JUI9bbvAPj3mVcb5MiWbzJv\n" +
            "Hnyw0tASHFl1VxYYd5ZuFS5o2JNkkFdZW6HaXqkUus9TrUYvkaMisppiBTkd6TnGmKWVRT0+adpE\n" +
            "PVCO+BdAK9Ur5rLOf5EAtq61n3aZK884WxTe9/hzvu0LClwBo+FI92YzQp91JpNqSs2iTcevi1kT\n" +
            "ELj9F0y0jpLKVQ/au/x7EivxrApfzOjNUn727HQ1+LjakUlpdv0mqlOAY3rSE2H2Y3/mX9o6y4b6\n" +
            "4PmhKk32mZoZ2bnrGmO/iM47alVYxvM75DEcTG4yc5KQ0OadndZotwQgvo6YaG1dXP4i6wznzb7n\n" +
            "Jf7gSOJrQWMKVNoSBsxf5foqvEOk38AE42n2DAtAhUAgpy4Zks9LuAOEFWQuG755vGTVfQCFFl4N\n" +
            "NjkEYiw6SSVkFCVeLalMtUEX02rn";

    static final String INVALID_LICENSE = StringUtils.repeat("Helloworld ", 50);

    private LicenseHandler handler;

    public void setHandler(LicenseHandler handler)
    {
        this.handler = handler;
    }

    @Test
    public void testLicenseHandlerAvailable()
    {
        assertNotNull("License handler must be available to plugins", handler);
    }

    @Test
    public void testValidLicenseShouldNotThrowException()
    {
        // this should not throw any exception.
        handler.setLicense(VALID_LICENSE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLicenseShouldthrowIAE()
    {
        // Should throw IllegalArgumentException for invalid license.
        handler.setLicense(INVALID_LICENSE);
    }
}