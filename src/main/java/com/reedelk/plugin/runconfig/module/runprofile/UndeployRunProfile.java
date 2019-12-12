package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.service.module.RuntimeApi;
import com.reedelk.plugin.service.module.RuntimeApi.OperationCallback;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class UndeployRunProfile extends AbstractRunProfile {

    public UndeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {
        Module module = Optional.ofNullable(ModuleManager.getInstance(project).findModuleByName(moduleName))
                .orElseThrow(() -> new ExecutionException(message("module.run.error.module.not.found", moduleName)));

        // Un Deploy Module
        RuntimeApi.getInstance(module).delete(moduleFile, address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                SourceChangeService.getInstance(project).changed(runtimeConfigName, moduleName);
                String message = message("module.run.uninstall", module.getName());
                ToolWindowUtils.notifyInfo(module.getProject(), message, runtimeConfigName);
            }

            @Override
            public void onError(Exception exception) {
                SourceChangeService.getInstance(project).changed(runtimeConfigName, moduleName);
                String message = message("module.run.uninstall.error", module.getName(), exception.getMessage());
                ToolWindowUtils.notifyError(module.getProject(), message, runtimeConfigName);
            }
        });

        return null;
    }
}
