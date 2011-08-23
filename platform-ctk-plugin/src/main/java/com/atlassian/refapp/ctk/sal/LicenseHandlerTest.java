package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.license.LicenseHandler;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LicenseHandlerTest extends SpringAwareTestCase
{
    static final String VALID_LICENSE =
            "AAACVA0ODAoPeNqdVk1vozAQvfMrkPZMBE20h0pI2ya0i5qv3dBLb4ZMEq/ARLbJbv79gsHlywbRU\n" +
            "xR/zJt5783gb8ElM18gNB3bdJzHuf04/256h8B8sB3HCFESpulsjSMgDLx/V0zvK8TB3c9/GgegN\n" +
            "6D+yn1eLDaWvdjZ1sfh4816dfa/jIimf4+zJQXEcUrEnSKiZT9Yjp3vZhEOY5gtU8JRxLcoAdcnH\n" +
            "M5UnDcDQInxB1MksYP7FcSpIE/O375WAPldfAOX0wzqoHtEOQEqjndLOOIivvu+XfsbP/BWxgmzC\n" +
            "9xHMinBdvSMCGZi3X3iMWIMI1LjejcUZ+VuO6ENwnlEgkjU5VCit1IWdWvAxJ4+XpSSU5uUYkHHY\n" +
            "cXNNktCoLvTs/i7TiMUP52BcOZaThlADyjy0crcAFfdWwGLKL6KEqtj5iml5j5GPP9NzGXwZnJgH\n" +
            "JNzzaY6oMCaGLAQtStaxUlfS+HncZMMaCP2B8yp8XgF6uWRYxfXqNYRbuwHktaYRWlSsjBknaY3x\n" +
            "IIU/50BLQUv08xodEEMupJWGesUL+0wIWHZACMWnSZsQcLo6NGwJBPS+GKIW7UxZcAm88rG+w1Jy\n" +
            "qHReZ9DUltKodQw6mBr9rQXZeikb9WonaPD6Wjo6/BRe1EoOcX/krNeKJnByOwc8b3ye/OlBCc5W\n" +
            "nbdBKDqiq6e7jTtNZ7k6wsjVT/OKkxFK4zIMv5OaM3VUkrFpO2fUg96xVSUjwsNoZ13xKThp+7Dw\n" +
            "Zo/Hw4jc3oohnIK5bqSxndgkvwi7S6h/wEUMrYWMCwCFFBwk1DQL0qqxQbV15RZa/W8ESB7AhQ3b\n" +
            "E9VSUvHT1S7vDj6p4zb0Oabdw==X02rr";

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

    @Test
    public void testGetServerIdReturnsValidID()
    {
        assertTrue(handler.getServerId().matches("A[A-Z0-9]{3}(-[A-Z0-9]{4}){3}"));
    }
}