package com.atlassian.refapp.sal.user;

import java.net.URI;

import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;

import static com.google.common.base.Preconditions.checkNotNull;

public class RefimplUserProfile implements UserProfile
{
    private static final URI SMALL_CHARLIE = URI.create("/charlie_sm.png");
    private static final URI MEDIUM_CHARLIE = URI.create("/charlie_md.png");
    private static final URI LARGE_CHARLIE = URI.create("/charlie_lg.png");

    private final String username;
    private final String fullName;
    private final String email;
    private final URI profilePageUri;

    public RefimplUserProfile(User user)
    {
        this.username = checkNotNull(user.getName(), "username");
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.profilePageUri = URI.create("/plugins/servlet/users/" + username);
    }

    public String getUsername()
    {
        return username;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public URI getProfilePictureUri(int width, int height)
    {
        if (width < 16 || height < 16)
        {
            return null;
        }
        else if (width < 48 && height < 48)
        {
            return SMALL_CHARLIE;
        }
        else if (width < 100 && height < 100)
        {
            return MEDIUM_CHARLIE;
        }
        else
        {
            return LARGE_CHARLIE;
        }
    }

    public URI getProfilePictureUri()
    {
        return LARGE_CHARLIE;
    }

    public URI getProfilePageUri()
    {
        return profilePageUri;
    }
}
