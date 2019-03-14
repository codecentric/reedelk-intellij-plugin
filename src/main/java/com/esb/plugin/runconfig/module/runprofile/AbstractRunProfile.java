package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.service.application.http.ESBHttpService;
import com.esb.plugin.utils.ESBModuleUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
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

    AbstractRunProfile(final Project project, final String moduleName) {
        this.project = project;
        this.moduleName = moduleName;
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

        return execute(mavenProject, moduleJarFilePath);
    }

    protected abstract ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException;

    void switchToRunToolWindow() {
        ToolWindowManager
                .getInstance(project)
                .getToolWindow(ToolWindowId.RUN).show(null);
    }

    // TODO: Url how to determine?
    <T> T post(String api, String json, Function<String, T> responseMapper) throws ExecutionException {

        String url = "http://localhost:9988/" + api;

        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            String result = ESBHttpService.post(url, json, ESBHttpService.JSON);
            return responseMapper.apply(result);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    <T> T delete(String api, String json, Function<String, T> responseMapper) throws ExecutionException {

        String url = "http://localhost:9988/" + api;

        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            String result = ESBHttpService.delete(url, json, ESBHttpService.JSON);
            return responseMapper.apply(result);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
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
