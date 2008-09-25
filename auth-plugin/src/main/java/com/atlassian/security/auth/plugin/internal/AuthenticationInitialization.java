package com.atlassian.security.auth.plugin.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.security.password.PasswordEncryptor;

public class AuthenticationInitialization
{
    private final Log log = LogFactory.getLog(getClass());
    
    public AuthenticationInitialization(UserManager userManager, GroupManager groupManager, PasswordEncryptor passwordEncryptor)
    {
        Services.setUserManager(userManager);
        Services.setGroupManager(groupManager);
        Services.setPasswordEncryptor(passwordEncryptor);
     
        try
        {
            addUser(userManager, groupManager, "admin", "admin", "users", "administrators");
            addUser(userManager, groupManager, "fred", "fred", "users", "administrators");
            addUser(userManager, groupManager, "barney", "barney", "users");
        }
        catch (EntityException e)
        {
            log.error("Failed setting up default users", e);
        }
    }

    private void addUser(UserManager userManager, GroupManager groupManager, 
        String username, String password, String... groupNames) throws EntityException
    {
        User user = userManager.createUser(username);
        userManager.alterPassword(user, password);
        for (String groupName : groupNames)
        {
            Group group = getOrCreateGroupIfAbsent(groupManager, groupName);
            groupManager.addMembership(group, user);
        }
    }

    private Group getOrCreateGroupIfAbsent(GroupManager groupManager, String groupName) throws EntityException
    {
        Group group = groupManager.getGroup(groupName);
        if (group == null)
        {
            group = groupManager.createGroup(groupName);
        }
        return group;
    }
}
