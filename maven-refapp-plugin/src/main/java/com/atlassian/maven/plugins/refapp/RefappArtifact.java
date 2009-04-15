package com.atlassian.maven.plugins.refapp;

/**
 * Represents a plugin artifact to be retrieved
 */
public class RefappArtifact {

    /**
     * @parameter
     * @required
     */
    private String groupId;

    /**
     * @parameter
     * @required
     */
    private String artifactId;

    /**
     * @parameter
     * @required
     */
    private String version;

    public RefappArtifact() {
    }

    public RefappArtifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public RefappArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
