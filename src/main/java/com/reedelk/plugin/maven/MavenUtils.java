package com.reedelk.plugin.maven;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MavenUtils {

    private static final Logger LOG = Logger.getInstance(MavenUtils.class);

    private MavenUtils() {
    }

    public static boolean existsMavenArtifact(Project project, String moduleName) {
        return getMavenProject(project, moduleName).map(mavenProject ->
                getModuleJarFile(mavenProject).toFile().exists())
                .orElse(false);
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

    public static Path getModuleJarFile(MavenProject mavenProject) {
        String targetDir = mavenProject.getBuildDirectory();
        MavenId mavenId = mavenProject.getMavenId();
        return Paths.get(targetDir, mavenId.getArtifactId() + "-" + mavenId.getVersion() + ".jar");
    }

    public static String getModuleJarFileURI(MavenProject mavenProject) {
        return getModuleJarFile(mavenProject).toUri().toString();
    }

    public static boolean containsAnySourceFile(Project project, String moduleName) {
        return getMavenProject(project, moduleName).map(mavenProject -> {
            List<String> sources = mavenProject.getSources();
            return sources.stream().anyMatch(sourcesDirectory -> {
                try {
                    List<Path> collect = Files.walk(Paths.get(sourcesDirectory))
                            .filter(path -> path.toFile().isFile())
                            .collect(Collectors.toList());
                    return !collect.isEmpty();
                } catch (IOException exception) {
                    // The java directory does not exists. Therefore we are ok.
                    LOG.warn(message("error.reading.sources.directory", exception.getMessage()));
                    return false;
                }
            });
        }).orElse(false);
    }
}
