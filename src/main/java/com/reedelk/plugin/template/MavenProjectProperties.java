package com.reedelk.plugin.template;

import com.reedelk.plugin.message.ReedelkBundle;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.Properties;

public class MavenProjectProperties extends Properties {

    public MavenProjectProperties(MavenId projectId, String sdkVersion) {
        setProperty("groupId", projectId.getGroupId());
        setProperty("artifactId", projectId.getArtifactId());
        setProperty("version", projectId.getVersion());
        setProperty("javaVersion", sdkVersion);
        setProperty("reedelkVersion", ReedelkBundle.message("reedelk.version"));
    }

    public MavenProjectProperties(MavenId projectId, String sdkVersion, MavenId parentId) {
        this(projectId, sdkVersion);
        setProperty("parentId", parentId.getArtifactId());
        setProperty("parentVersion", parentId.getVersion());
    }
}
