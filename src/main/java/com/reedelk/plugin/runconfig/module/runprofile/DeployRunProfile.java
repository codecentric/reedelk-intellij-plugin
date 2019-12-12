package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.service.module.RuntimeApi;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.plugin.commons.Defaults.PROJECT_RESOURCES_FOLDER;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.RuntimeApi.OperationCallback;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {
        Module module = Optional.ofNullable(ModuleManager.getInstance(project).findModuleByName(moduleName))
                .orElseThrow(() -> new ExecutionException(message("module.run.error.module.not.found", moduleName)));

        if (SourceChangeService.getInstance(project).isHotSwap(runtimeConfigName, moduleName)) {
            // If the module has changes only in Flows/Configs/Scripts and so on, then we can hot swap the module.
            hotSwap(mavenProject, moduleFile, module);
        } else {
            // Otherwise we re-deploy the module
            deploy(moduleFile, module);
        }

        return null;
    }

    private void deploy(@NotNull String moduleFile, Module module) {
        RuntimeApi.getInstance(module).deploy(moduleFile, address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                String message = message("module.run.deploy", module.getName());
                ToolWindowUtils.notifyInfo(module.getProject(), message, runtimeConfigName);
                SourceChangeService.getInstance(project).unchanged(runtimeConfigName, moduleName);
            }

            @Override
            public void onError(Exception exception) {
                String message = message("module.run.deploy.error", module.getName(), exception.getMessage());
                ToolWindowUtils.notifyError(module.getProject(), message, runtimeConfigName);
            }
        });
    }

    private void hotSwap(@NotNull MavenProject mavenProject, @NotNull String moduleFile, Module module) {
        String mavenDirectory = mavenProject.getDirectory();
        String resourcesRootDirectory = Paths.get(mavenDirectory, PROJECT_RESOURCES_FOLDER).toString();

        RuntimeApi.getInstance(module).hotSwap(moduleFile, resourcesRootDirectory, address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                String message = message("module.run.update", module.getName());
                ToolWindowUtils.notifyInfo(module.getProject(), message, runtimeConfigName);
            }

            @Override
            public void onError(Exception exception) {
                String message = message("module.run.update.error", module.getName(), exception.getMessage());
                ToolWindowUtils.notifyError(module.getProject(), message, runtimeConfigName);
            }
        });
    }
}
