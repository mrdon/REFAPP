package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.sal.api.user.UserManager;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class UserManagerTest extends SpringAwareTestCase
{
    private UserManager userManager;
    private AppSpecificInfoProvider infoProvider;

    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    public void setInfoProvider(AppSpecificInfoProvider infoProvider)
    {
        this.infoProvider = infoProvider;
    }

    @Test
    public void testInjection()
    {
        assertTrue("UserManager should be injectable", userManager != null);
    }

    @Test
    public void testGetUsers()
    {
        final String remoteUsername = userManager.getRemoteUsername();
		assertTrue("Should return null for username when not logged in. Currently logged user: " + remoteUsername, remoteUsername == null);
        assertTrue("Should be able to login with admin/admin", userManager.authenticate(infoProvider.getAdminUsername(), infoProvider.getAdminPassword()));
        assertTrue("admin user should be sysadmin", userManager.isSystemAdmin(infoProvider.getAdminUsername()));
        assertTrue("somedumbadmin user should not be sysadmin", !userManager.isSystemAdmin("some_dumb_admin_blah_blah_wahaha"));
    }
}