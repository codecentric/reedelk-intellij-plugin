package de.codecentric.reedelk.plugin.template;

import org.jetbrains.idea.maven.model.MavenId;

import java.util.Properties;

public class MavenProjectProperties extends Properties {

    public MavenProjectProperties(MavenId projectId, String sdkVersion, String reedelkRuntimeVersion) {
        setProperty("groupId", projectId.getGroupId());
        setProperty("artifactId", projectId.getArtifactId());
        setProperty("version", projectId.getVersion());
        setProperty("javaVersion", sdkVersion);
        setProperty("reedelkVersion", reedelkRuntimeVersion);
    }

    public MavenProjectProperties(MavenId projectId, String sdkVersion, String reedelkRuntimeVersion, MavenId parentId) {
        this(projectId, sdkVersion, reedelkRuntimeVersion);
        setProperty("parentId", parentId.getArtifactId());
        setProperty("parentVersion", parentId.getVersion());
    }
}
