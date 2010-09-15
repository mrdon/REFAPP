package com.atlassian.refapp.ctk.test;

import java.util.Collection;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.project.ProjectManager;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ProjectManagerTest extends SpringAwareTestCase
{
    private ProjectManager projectManager;

    public void setProjectManager(ProjectManager projectManager)
    {
        this.projectManager = projectManager;
    }

    @Test
    public void testInjection()
    {
        assertTrue("ProjectManager should be injectable", projectManager != null);
    }

    @Test
    public void testGetAllProjectKeys()
    {
        final Collection<String> keys = projectManager.getAllProjectKeys();
        assertTrue("Project manager should return keys: " + keys, keys != null);
    }
}