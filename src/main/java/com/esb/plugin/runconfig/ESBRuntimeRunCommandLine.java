package com.esb.plugin.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class ESBRuntimeRunCommandLine extends JavaCommandLineState {

    private final ESBRuntimeRunConfiguration configuration;

    protected ESBRuntimeRunCommandLine(@NotNull ESBRuntimeRunConfiguration configuration, @NotNull ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParams = new JavaParameters();
        Project project = configuration.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());

        String homeDirectory = configuration.getRuntimeHomeDirectory();
        javaParams.setJarPath(Paths.get(homeDirectory, "bin", "runtime-1.0.0-SNAPSHOT.jar").toString());
        return javaParams;
    }
}
