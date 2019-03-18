package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.service.application.rest.RESTModuleService;
import com.esb.plugin.service.project.filechange.ESBFileChangeService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {

        RESTModuleService service = new RESTModuleService(address, port);

        // Check if we can hot swap the module flows.
        if (ESBFileChangeService.getInstance(project).isHotSwap(runtimeConfigName, moduleName)) {
            String mavenDirectory = mavenProject.getDirectory();
            Path resourcesRootDirectory = Paths.get(mavenDirectory, "src", "main", "resources");

            service.hotSwap(moduleFile, resourcesRootDirectory.toString());

            String message = format("Module <b>%s</b> reloaded", moduleName);
            switchToolWindowAndNotifyWithMessage(message);


            // Otherwise we re-deploy the module
        } else {
            service.deploy(moduleFile);

            ESBFileChangeService.getInstance(project).unchanged(runtimeConfigName, moduleName);

            String message = format("Module <b>%s</b> updated", moduleName);
            switchToolWindowAndNotifyWithMessage(message);
        }

        return null;
    }


}
