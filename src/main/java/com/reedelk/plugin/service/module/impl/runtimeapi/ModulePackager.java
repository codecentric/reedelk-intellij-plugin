package com.reedelk.plugin.service.module.impl.runtimeapi;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.maven.MavenPackageGoal;
import com.reedelk.plugin.maven.MavenUtils;
import org.jetbrains.idea.maven.model.MavenResource;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

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
                    String error = message("error.maven.goal.package", moduleName);
                    callback.onError(new PluginException(error));
                } else {
                    // The  Maven package goal was successful. The .jar artifact is in the /target folder and we can
                    // deploy the package onto the ESB runtime.
                    callback.onDone();
                }
            });
            packageGoal.execute();

        } else {
            Optional<MavenProject> optionalMavenProject = MavenUtils.getMavenProject(project, moduleName);
            if (optionalMavenProject.isPresent()) {
                buildModuleJar(optionalMavenProject.get());
            } else {
                String error = message("module.run.error.maven.project.not.found", moduleName);
                callback.onError(new PluginException(error));
            }
        }
    }

    private void buildModuleJar(MavenProject mavenProject) {
        // Only flows and resources. we create the package.
        String version = mavenProject.getMavenId().getVersion();
        String artifactId = mavenProject.getMavenId().getArtifactId();
        List<MavenResource> resources = mavenProject.getResources();

        Optional<MavenResource> mavenResource = resources.stream().findFirst();
        if (mavenResource.isPresent()) {
            String resourcesDirectory = mavenResource.get().getDirectory();
            String moduleJarFilePath = MavenUtils.getModuleJarFile(mavenProject).toString();
            ModuleJarBuilder moduleJarBuilder = new ModuleJarBuilder(moduleJarFilePath, resourcesDirectory, version, artifactId);
            try {
                moduleJarBuilder.run();
                callback.onDone();
            } catch (Exception exception) {
                callback.onError(exception);
            }
        } else {
            callback.onError(new PluginException(message("error.resource.dir.not.found")));
        }
    }

    public interface OnModulePackaged {
        void onDone();
        void onError(Exception exception);
    }
}
