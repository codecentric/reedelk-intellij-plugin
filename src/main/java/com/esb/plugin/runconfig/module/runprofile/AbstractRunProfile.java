package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.commons.MavenUtils;
import com.esb.plugin.commons.NotificationUtils;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.esb.plugin.service.project.ToolWindowService;
import com.intellij.execution.*;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

import java.io.File;
import java.util.Optional;

import static java.lang.String.format;

abstract class AbstractRunProfile implements RunProfileState {

    protected final Project project;
    protected final String moduleName;
    protected final String runtimeConfigName;

    String address;
    int port;

    AbstractRunProfile(final Project project, final String moduleName, String runtimeConfigName) {
        this.project = project;
        this.moduleName = moduleName;
        this.runtimeConfigName = runtimeConfigName;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {

        Optional<MavenProject> optionalMavenProject = MavenUtils.getMavenProject(project, moduleName);
        if (!optionalMavenProject.isPresent()) {
            throw new ExecutionException("Maven project could not be found");
        }

        MavenProject mavenProject = optionalMavenProject.get();
        String moduleJarFilePath = getModuleJarFile(mavenProject);

        RunnerAndConfigurationSettings configSettings = RunManager.getInstance(project).findConfigurationByName(runtimeConfigName);
        if (configSettings == null) throw new ExecutionException("Could not find config with name = " + runtimeConfigName + ", check module run configuration");
        ESBRuntimeRunConfiguration runtimeRunConfiguration = (ESBRuntimeRunConfiguration) configSettings.getConfiguration();

        this.port = Integer.parseInt(runtimeRunConfiguration.getRuntimePort());
        this.address = runtimeRunConfiguration.getRuntimeBindAddress();

        return execute(mavenProject, moduleJarFilePath);
    }

    protected abstract ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException;


    void switchToolWindowAndNotifyWithMessage(String message) {
        ToolWindowService toolWindowService = ServiceManager.getService(project, ToolWindowService.class);
        Optional<String> optionalToolWindowId = toolWindowService.get(runtimeConfigName);
        optionalToolWindowId.ifPresent(toolWindowId -> getToolWindowById(toolWindowId).show(null));
        optionalToolWindowId.ifPresent(toolWindowId -> NotificationUtils.notifyInfo(toolWindowId, message, project));
    }

    private ToolWindow getToolWindowById(String toolWindowId) {
        return ToolWindowManager
                .getInstance(project)
                .getToolWindow(toolWindowId);
    }

    private String getModuleJarFile(MavenProject mavenProject) {
        String targetDir = mavenProject.getBuildDirectory();
        MavenId mavenId = mavenProject.getMavenId();
        return format("file:%s" + File.separator + "%s-%s.jar",
                targetDir,
                mavenId.getArtifactId(),
                mavenId.getVersion());
    }
}
