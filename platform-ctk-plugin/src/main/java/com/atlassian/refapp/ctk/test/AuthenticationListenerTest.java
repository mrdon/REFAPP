package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.auth.AuthenticationListener;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class AuthenticationListenerTest extends SpringAwareTestCase
{
	private AuthenticationListener authenticationListener;

    public void setAuthenticationListener(AuthenticationListener authenticationListener)
    {
        this.authenticationListener = authenticationListener;
    }

    @Test
    public void testInjection()
	{
		assertTrue("AuthenticationListener should be injectable", authenticationListener != null);
	}
}