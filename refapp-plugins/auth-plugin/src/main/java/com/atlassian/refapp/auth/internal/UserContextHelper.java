package com.atlassian.refapp.auth.internal;

import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.user.*;

import java.security.Principal;

public class UserContextHelper
{
    private final AuthenticationContext authenticationContext;
    private final UserManager userManager;
    private final GroupManager groupManager;

    public UserContextHelper(AuthenticationContext authenticationContext, UserManager userManager,
        GroupManager groupManager)
    {
        this.authenticationContext = authenticationContext;
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    public User getRemoteUser()
    {
        Principal principal = authenticationContext.getUser();
        if (principal == null)
        {
            return null;
        }
        try
        {
            return userManager.getUser(principal.getName());
        }
        catch (EntityException ee)
        {
            return null;
        }
    }

    public boolean isRemoteUserSystemAdministrator()
    {
        User user = getRemoteUser();
        if (user == null)
        {
            return false;
        }
        try
        {
            Group group = groupManager.getGroup("administrators");
            if (group == null)
            {
                return false;
            }
            return groupManager.hasMembership(group, user);
        }
        catch (EntityException ee)
        {
            return false;
        }
    }

}
