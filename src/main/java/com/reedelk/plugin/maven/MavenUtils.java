package com.reedelk.plugin.maven;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.nio.file.Paths;
import java.util.Optional;

public class MavenUtils {

    private MavenUtils() {
    }

    public static Optional<MavenProject> getMavenProject(Module module) {
        return Optional.ofNullable(MavenProjectsManager.getInstance(module.getProject()).findProject(module));
    }

    public static Optional<MavenProject> getMavenProject(Project project, String moduleName) {
        Module module = findModuleByName(moduleName, project);
        return getMavenProject(module);
    }

    private static Module findModuleByName(String moduleName, Project project) {
        return ModuleManager.getInstance(project).findModuleByName(moduleName);
    }

    public static String getModuleJarFile(MavenProject mavenProject) {
        String targetDir = mavenProject.getBuildDirectory();
        MavenId mavenId = mavenProject.getMavenId();
        return Paths.get(targetDir, mavenId.getArtifactId() + "-" + mavenId.getVersion() + ".jar")
                .toUri()
                .toString();
    }
}