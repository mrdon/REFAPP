package com.atlassian.refapp.auth.internal;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.SecurityConfig;

public class StaticDelegatingRoleMapper implements RoleMapper
{
    private static RoleMapper roleMapper;
    
    static void setRoleMapper(RoleMapper roleMapper)
    {
        StaticDelegatingRoleMapper.roleMapper = roleMapper;
    }

    public void init(Map params, SecurityConfig securityConfig)
    {
        roleMapper.init(params, securityConfig);
    }

    public boolean canLogin(Principal user, HttpServletRequest request)
    {
        return roleMapper.canLogin(user, request);
    }

    public boolean hasRole(Principal user, HttpServletRequest request, String role)
    {
        return roleMapper.hasRole(user, request, role);
    }
}
