package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.sal.api.user.UserManager;

import com.atlassian.sal.api.user.UserProfile;
import org.junit.Test;

import java.security.Principal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

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
    public void testAuthentication()
    {
        final String remoteUsername = userManager.getRemoteUsername();
        assertTrue("Should return null for username when not logged in. Currently logged user: " + remoteUsername, remoteUsername == null);
        assertTrue("Should be able to login with admin/admin", userManager.authenticate(infoProvider.getAdminUsername(), infoProvider.getAdminPassword()));

        assertFalse("Should not be able to login with random user/password", userManager.authenticate("random user name wahaha", "random user password wahaha"));
    }

    @Test
    public void testUserGroupCheck()
    {
        assertTrue("admin user should be sysadmin", userManager.isSystemAdmin(infoProvider.getAdminUsername()));
        assertFalse("admin user should not be in a random dumb group name", userManager.isUserInGroup(infoProvider.getAdminUsername(), "some_dumb_group_name_wahaha"));

        assertFalse("some random dumb user should not be sysadmin", userManager.isSystemAdmin("some_random_dumb_user_blah_blah_wahaha"));
    }

    @Test
    public void testGetProfile()
    {
        UserProfile nullProfile = userManager.getUserProfile("something_which_doesnt_exist");
        assertNull("user which doesn't exist should result in null profile", nullProfile);

        UserProfile adminProfile = userManager.getUserProfile(infoProvider.getAdminUsername());
        assertNotNull("admin profile should not be null", adminProfile);

        assertEquals("admin profile should have appropriate admin username", infoProvider.getAdminUsername(), adminProfile.getUsername());
        assertTrue(adminProfile.getFullName().contains("(Sysadmin)"));
        assertTrue("this should return an email address", adminProfile.getEmail().contains("@"));
    }

    @Test
    public void testResolve()
    {
        Principal nullPrincipal = userManager.resolve("something_which_doesnt_exist");
        assertNull("when the user doesn't exist, this must return null", nullPrincipal);

        Principal adminPrincipal = userManager.resolve(infoProvider.getAdminUsername());
        assertNotNull(adminPrincipal);
        assertEquals(infoProvider.getAdminUsername(), adminPrincipal.getName());
    }
}
