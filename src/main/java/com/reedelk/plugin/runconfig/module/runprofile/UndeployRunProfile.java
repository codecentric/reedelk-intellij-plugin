package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import static java.lang.String.format;

public class UndeployRunProfile extends AbstractRunProfile {

    public UndeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {
        Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        RestService service = new RestService(project, module, address, port);

        // Un Deploy Module
        service.delete(moduleFile);

        SourceChangeService.getInstance(project).changed(runtimeConfigName, moduleName);

        String message = format("Module <b>%s</b> uninstalled", moduleName);
        switchToolWindowAndNotifyWithMessage(message);

        return null;
    }
}
