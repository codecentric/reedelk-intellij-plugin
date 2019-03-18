package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.esb.plugin.service.application.http.ESBHttpService;
import com.esb.plugin.service.application.http.HttpResponse;
import com.esb.plugin.service.project.toolwindow.ESBToolWindowService;
import com.esb.plugin.utils.ESBModuleUtils;
import com.esb.plugin.utils.ESBNotification;
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
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

abstract class AbstractRunProfile implements RunProfileState {

    protected final Project project;
    protected final String moduleName;
    protected final String runtimeConfigName;
    protected int port;

    AbstractRunProfile(final Project project, final String moduleName, String runtimeConfigName) {
        this.project = project;
        this.moduleName = moduleName;
        this.runtimeConfigName = runtimeConfigName;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {

        Optional<MavenProject> optionalMavenProject = ESBModuleUtils.getMavenProject(moduleName, project);
        if (!optionalMavenProject.isPresent()) {
            throw new ExecutionException("Maven project could not be found");
        }

        MavenProject mavenProject = optionalMavenProject.get();
        String moduleJarFilePath = getModuleJarFile(mavenProject);

        RunnerAndConfigurationSettings configSettings = RunManager.getInstance(project).findConfigurationByName(runtimeConfigName);
        if (configSettings == null) throw new ExecutionException("Could not find config with name = " + runtimeConfigName + ", check module run configuration");
        ESBRuntimeRunConfiguration runtimeRunConfiguration = (ESBRuntimeRunConfiguration) configSettings.getConfiguration();
        this.port = Integer.parseInt(runtimeRunConfiguration.getRuntimePort());
        // TODO: add host
        return execute(mavenProject, moduleJarFilePath);
    }

    protected abstract ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException;


    HttpResponse post(String url, String json) throws ExecutionException {
        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            return ESBHttpService.post(url, json, ESBHttpService.JSON);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    <T> T delete(String api, String json, Function<String, T> responseMapper) throws ExecutionException {

        String url = String.format("http://localhost:%d/", port) + api;

        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            HttpResponse result = ESBHttpService.delete(url, json, ESBHttpService.JSON);
            if (result.isSuccessful()) {
                return responseMapper.apply(result.getBody());
            } else {
                throw new ExecutionException(result.getBody());
            }
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    void switchToolWindowAndNotifyWithMessage(String message) {
        ESBToolWindowService toolWindowService = ServiceManager.getService(project, ESBToolWindowService.class);
        Optional<String> optionalToolWindowId = toolWindowService.get(runtimeConfigName);
        optionalToolWindowId.ifPresent(toolWindowId -> getToolWindowById(toolWindowId).show(null));
        optionalToolWindowId.ifPresent(toolWindowId -> ESBNotification.notifyInfo(toolWindowId, message, project));
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
