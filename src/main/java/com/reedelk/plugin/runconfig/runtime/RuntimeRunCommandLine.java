package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.runconfig.runtime.parameter.ParameterStrategyBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

import static java.lang.String.format;

public class RuntimeRunCommandLine extends JavaCommandLineState {

    private final RuntimeRunConfiguration configuration;

    protected RuntimeRunCommandLine(@NotNull RuntimeRunConfiguration configuration, @NotNull ExecutionEnvironment environment) {
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

        ParameterStrategyBuilder.from(manager.getProjectSdk()).apply(parameters, configuration);

        String runtimeHomeDirectory = configuration.getRuntimeHomeDirectory();

        String jarPath = getJarPath(runtimeHomeDirectory);

        javaParams.setJarPath(jarPath);

        return javaParams;
    }

    private String getJarPath(String runtimeHomeDirectory) throws ExecutionException {
        String runtimeJarName = FileUtils
                .findRuntimeJarName(runtimeHomeDirectory)
                .orElseThrow(() -> new ExecutionException(format("Could not find suitable runtime (home directory: %s)", runtimeHomeDirectory)));
        return Paths.get(runtimeHomeDirectory, "bin", runtimeJarName).toString();
    }
}
