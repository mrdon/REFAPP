package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.refapp.ctk.AppSpecificInfoProviderFactory;
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
    private AppSpecificInfoProvider infoProvider = AppSpecificInfoProviderFactory.create();

    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    @Test
    public void testUserManager()
    {
        assertNotNull("UserManager must be available to plugins", userManager);
    }

    @Test
    public void testNullRemoteUserWhileNotLoggedIn()
    {
        final String remoteUsername = userManager.getRemoteUsername();
        assertTrue("Should return null for username when not logged in. Currently logged user: " + remoteUsername, remoteUsername == null);
    }

    @Test
    public void testAdminShouldBeAuthenticated()
    {
        assertTrue("Should be able to login with admin/admin", userManager.authenticate(infoProvider.getAdminUsername(), infoProvider.getAdminPassword()));
    }

    @Test
    public void testRandomUserShouldNotBeAuthenticated()
    {
        assertFalse("Should not be able to login with random user/password", userManager.authenticate("random user name wahaha", "random user password wahaha"));
    }

    @Test
    public void testAdminUserMustBeInAdminGroup()
    {
        assertTrue("admin user should be sysadmin", userManager.isSystemAdmin(infoProvider.getAdminUsername()));
    }

    @Test
    public void testAdminUserMustNotBeInRandomGroup()
    {
        assertFalse("admin user should not be in a random dumb group name", userManager.isUserInGroup(infoProvider.getAdminUsername(), "some_dumb_group_name_wahaha"));
    }

    @Test
    public void testRandomUserMustNotBeAdmin()
    {
        assertFalse("some random user should not be sysadmin", userManager.isSystemAdmin("some_random_dumb_user_blah_blah_wahaha"));
    }


    @Test
    public void testGetNonExistingProfileShouldReturnNull()
    {
        UserProfile nullProfile = userManager.getUserProfile("something_which_doesnt_exist");
        assertNull("user which doesn't exist should result in null profile", nullProfile);
    }

    @Test
    public void testGetAdminProfileShouldNotBeNull()
    {
        UserProfile adminProfile = userManager.getUserProfile(infoProvider.getAdminUsername());
        assertNotNull("admin profile should not be null", adminProfile);
    }

    @Test
    public void testGetAdminProfileShouldReturnValidProfile()
    {
        UserProfile adminProfile = userManager.getUserProfile(infoProvider.getAdminUsername());
        assertEquals("admin profile should have appropriate admin username", infoProvider.getAdminUsername(), adminProfile.getUsername());
        assertTrue(adminProfile.getFullName().contains("(Sysadmin)"));
        assertTrue("this should return an email address", adminProfile.getEmail().contains("@"));
    }

    @Test
    public void testResolveNotExistingUserShouldReturnNull()
    {
        Principal nullPrincipal = userManager.resolve("something_which_doesnt_exist");
        assertNull("when the user doesn't exist, this must return null", nullPrincipal);
    }

    @Test
    public void testResolveAdminShouldReturnAdminPrinciple()
    {
        Principal adminPrincipal = userManager.resolve(infoProvider.getAdminUsername());
        assertNotNull(adminPrincipal);
        assertEquals(infoProvider.getAdminUsername(), adminPrincipal.getName());
    }
}
