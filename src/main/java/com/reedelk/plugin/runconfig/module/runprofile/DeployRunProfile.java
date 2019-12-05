package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {
        Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        RestService service = new RestService(project, module, address, port);

        // Check if we can hot swap the module flows.
        if (SourceChangeService.getInstance(project).isHotSwap(runtimeConfigName, moduleName)) {
            String mavenDirectory = mavenProject.getDirectory();
            Path resourcesRootDirectory = Paths.get(mavenDirectory, "src", "main", "resources");
            service.hotSwap(moduleFile, resourcesRootDirectory.toString(), runtimeConfigName);

            // Otherwise we re-deploy the module
        } else {
            service.deploy(moduleFile, runtimeConfigName);
            SourceChangeService.getInstance(project).unchanged(runtimeConfigName, moduleName);
        }

        return null;
    }
}
