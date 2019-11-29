package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.util.Optional;

import static java.lang.String.format;

public class MavenUtils {

    private MavenUtils() {
    }

    public static Optional<MavenProject> getMavenProject(Project project, String moduleName) {
        Module moduleByName = findModuleByName(moduleName, project);
        return Optional.ofNullable(MavenProjectsManager.getInstance(project).findProject(moduleByName));
    }

    private static Module findModuleByName(String name, Project project) {
        return ModuleManager.getInstance(project).findModuleByName(name);
    }

    public static String getModuleJarFile(MavenProject mavenProject) {
        String targetDir = mavenProject.getBuildDirectory();
        MavenId mavenId = mavenProject.getMavenId();
        return format("file:%s" + File.separator + "%s-%s.jar",
                targetDir,
                mavenId.getArtifactId(),
                mavenId.getVersion());
    }
}
