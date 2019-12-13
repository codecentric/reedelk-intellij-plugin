package com.reedelk.plugin.commons;

import org.jetbrains.idea.maven.model.MavenArtifact;

import java.util.function.Predicate;

public class ExcludedArtifactsFromModuleSync {

    private static final Predicate<MavenArtifact> EXCLUDE_OSGI = artifact -> !artifact.getGroupId().equals("org.osgi");
    private static final Predicate<MavenArtifact> EXCLUDE_OPS4J = artifact -> !artifact.getGroupId().equals("org.ops4j.pax.logging");
    private static final Predicate<MavenArtifact> EXCLUDE_PROJECT_REACTOR = artifact -> !artifact.getGroupId().equals("io.projectreactor");
    private static final Predicate<MavenArtifact> EXCLUSIONS = EXCLUDE_OSGI.and(EXCLUDE_OPS4J).and(EXCLUDE_PROJECT_REACTOR);

    private ExcludedArtifactsFromModuleSync() {
    }

    public static Predicate<MavenArtifact> predicate() {
        return EXCLUSIONS;
    }
}
