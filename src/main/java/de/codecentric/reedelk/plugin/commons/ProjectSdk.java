package de.codecentric.reedelk.plugin.commons;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;

import java.util.Optional;

public class ProjectSdk {

    private ProjectSdk() {
    }

    public static Optional<Sdk> of(Project project) {
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        return Optional.ofNullable(projectRootManager.getProjectSdk());
    }
}
