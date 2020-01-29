package com.reedelk.plugin.service.module.impl.runtimeapi;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.maven.MavenPackageGoal;
import com.reedelk.plugin.maven.MavenUtils;
import org.jetbrains.idea.maven.model.MavenResource;
import org.jetbrains.idea.maven.project.MavenProject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class ModulePackager {

    private final Project project;
    private final String moduleName;
    private final OnModulePackaged callback;

    public ModulePackager(Project project, String moduleName, OnModulePackaged callback) {
        this.callback = callback;
        this.project = project;
        this.moduleName = moduleName;
    }

    public void doPackage() {

        boolean containsAnySource = MavenUtils.containsAnySourceFile(project, moduleName);
        if (containsAnySource) {
            MavenPackageGoal packageGoal = new MavenPackageGoal(project, moduleName, result -> {
                if (!result) {
                    // Maven package goal was not successful
                    callback.onError(new PluginException("Maven package goal was not successful"));
                } else {
                    // The  Maven package goal was successful. The .jar artifact is in the /target folder and we can
                    // deploy the package onto the ESB runtime.
                    callback.onDone();
                }
            });
            packageGoal.execute();

        } else {
            Optional<MavenProject> optionalMavenProject = MavenUtils.getMavenProject(project, moduleName);
            MavenProject mavenProject = optionalMavenProject.get();

            // Only flows and resources. we create the package.
            String version = mavenProject.getMavenId().getVersion();
            String artifactId = mavenProject.getMavenId().getArtifactId();
            List<MavenResource> resources = mavenProject.getResources();

            Optional<MavenResource> first = resources.stream().findFirst();
            if (!first.isPresent()) {
                callback.onError(new PluginException("Resources directory does not exists"));
            }
            String resourcesDirectory = first.get().getDirectory();
            String moduleJarFilePath = MavenUtils.getModuleJarFile(mavenProject);
            String realPath = realPathFrom(moduleJarFilePath);
            ModuleJarBuilder moduleJarBuilder = new ModuleJarBuilder(realPath, resourcesDirectory, version, artifactId);
            try {
                moduleJarBuilder.run();
                callback.onDone();
            } catch (Exception exception) {
                callback.onError(exception);
            }
        }
    }

    private String realPathFrom(String moduleJarFilePath) {
        try {
            return Paths.get(new URI(moduleJarFilePath)).toString();
        } catch (URISyntaxException e) {
            throw new PluginException(String.format("Could not create real path from %s", moduleJarFilePath), e);
        }
    }

    public interface OnModulePackaged {
        void onDone();
        void onError(Exception exception);
    }
}