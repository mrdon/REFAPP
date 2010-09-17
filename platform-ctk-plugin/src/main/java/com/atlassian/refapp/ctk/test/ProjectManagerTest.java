package com.atlassian.refapp.ctk.test;

import java.util.Collection;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.project.ProjectManager;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class ProjectManagerTest extends SpringAwareTestCase
{
    private ProjectManager projectManager;

    public void setProjectManager(ProjectManager projectManager)
    {
        this.projectManager = projectManager;
    }

    @Test
    public void testProjectManagerAvailable()
    {
        assertNotNull("ProjectManager must be available for plugins", projectManager);
    }

    @Test
    public void testGetAllProjectKeysMustNeverReturnNull()
    {
        final Collection<String> keys = projectManager.getAllProjectKeys();
        assertNotNull("Project manager should return keys: " + keys, keys);
    }
}