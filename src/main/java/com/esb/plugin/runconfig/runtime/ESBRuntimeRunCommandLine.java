package com.esb.plugin.runconfig.runtime;

import com.esb.plugin.test.utils.ESBFileUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

import static java.lang.String.format;

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

        ParametersList parameters = javaParams.getVMParametersList();

        JavaVersion javaVersion = JavaVersion.from(manager.getProjectSdk());
        javaVersion.apply(parameters);

        parameters.add("-Dadmin.console.bind.port=" + configuration.getRuntimePort());

        String runtimeHomeDirectory = configuration.getRuntimeHomeDirectory();

        String jarPath = getJarPath(runtimeHomeDirectory);
        javaParams.setJarPath(jarPath);

        return javaParams;
    }

    private String getJarPath(String runtimeHomeDirectory) throws ExecutionException {
        String runtimeJarName = ESBFileUtils
                .findRuntimeJarName(runtimeHomeDirectory)
                .orElseThrow(() -> new ExecutionException(format("Could not find suitable runtime (home directory: %s)", runtimeHomeDirectory)));
        return Paths.get(runtimeHomeDirectory, "bin", runtimeJarName).toString();
    }
}
