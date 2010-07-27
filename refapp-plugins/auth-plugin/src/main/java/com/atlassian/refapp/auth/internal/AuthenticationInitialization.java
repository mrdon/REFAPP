package com.atlassian.refapp.auth.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;

public class AuthenticationInitialization
{
    private final Log log = LogFactory.getLog(getClass());
    
    public AuthenticationInitialization(UserManager userManager, GroupManager groupManager)
    {
        try
        {
            addUser(userManager, groupManager, "admin", "A. D. Ministrator", "admin@example.com", "admin", "users", "administrators");
            addUser(userManager, groupManager, "fred", "Fred Admin", "fred@example.org", "fred", "users", "administrators");
            addUser(userManager, groupManager, "barney", "Barney User", "barney@example.org", "barney", "users");
        }
        catch (EntityException e)
        {
            log.error("Failed setting up default users", e);
        }
    }

    private void addUser(UserManager userManager, GroupManager groupManager, 
        String username, String fullName, String email, String password, String... groupNames) throws EntityException
    {
        User user = userManager.createUser(username);
        user.setFullName(fullName);
        user.setEmail(email);
        userManager.saveUser(user);
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
