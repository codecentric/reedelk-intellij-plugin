package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.service.application.rest.RESTModuleService;
import com.esb.plugin.service.project.sourcechange.SourceChangeService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import static java.lang.String.format;

public class UndeployRunProfile extends AbstractRunProfile {


    public UndeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {

        RESTModuleService service = new RESTModuleService(address, port);

        // Un Deploy Module
        service.delete(moduleFile);

        SourceChangeService.getInstance(project).changed(runtimeConfigName, moduleName);

        String message = format("Module <b>%s</b> uninstalled", moduleName);
        switchToolWindowAndNotifyWithMessage(message);

        return null;

    }

}
