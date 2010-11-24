package com.atlassian.refapp.sal.auth;

import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.core.util.Assert;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.filter.BaseLoginFilter;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Fork of {@link com.atlassian.sal.core.auth.SeraphAuthenticationController} to use explicit rolemapper
 *
 * @since 2.7.0 
 */
public class RefappAuthenticationController implements AuthenticationController
{
    private final RoleMapper roleMapper;

    /**
     * @throws IllegalArgumentException if the roleMapper is <code>null</code>.
     */
    public RefappAuthenticationController(RoleMapper roleMapper)
    {
        this.roleMapper = Assert.notNull(roleMapper, "roleMapper");
    }

    /**
     * Checks the {@link RoleMapper} on whether or not the principal can login.
     *
     * @see AuthenticationController#canLogin(java.security.Principal , javax.servlet.http.HttpServletRequest)
     */
    public boolean canLogin(final Principal principal, final HttpServletRequest request)
    {
        return roleMapper.canLogin(principal, request);
    }

    /**
     * Checks the request attibutes for the {@link com.atlassian.seraph.filter.BaseLoginFilter#OS_AUTHSTATUS_KEY}. Will return <code>true</code> if
     * the key is not present.
     */
    public boolean shouldAttemptAuthentication(final HttpServletRequest request)
    {
        return request.getAttribute(BaseLoginFilter.OS_AUTHSTATUS_KEY) == null;
    }
}