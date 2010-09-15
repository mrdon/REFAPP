package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.auth.AuthenticationController;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class AuthenticationControllerTest extends SpringAwareTestCase
{
    private AuthenticationController controller;

    public void setController(AuthenticationController controller)
    {
        this.controller = controller;
    }

    @Test
    public void testInjection()
    {
        assertTrue("AuthenticationController should be injectable", controller != null);
    }
}
