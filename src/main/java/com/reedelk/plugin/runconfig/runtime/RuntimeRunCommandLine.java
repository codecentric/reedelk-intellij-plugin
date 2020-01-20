package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.reedelk.plugin.commons.ProjectSdk;
import com.reedelk.plugin.runconfig.runtime.parameter.ParameterStrategyBuilder;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.reedelk.plugin.commons.DefaultConstants.NameConvention;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeRunCommandLine extends JavaCommandLineState {

    private final RuntimeRunConfiguration configuration;

    RuntimeRunCommandLine(@NotNull RuntimeRunConfiguration configuration, @NotNull ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {


        Project project = configuration.getProject();

        Sdk sdk = ProjectSdk.of(project).orElseThrow(() ->
                new ExecutionException(message("error.sdk.not.selected")));

        JavaParameters javaParams = new JavaParameters();

        ParametersList parameters = javaParams.getVMParametersList();

        ParameterStrategyBuilder.from(sdk).apply(parameters, configuration);

        String runtimeHomeDirectory = configuration.getRuntimeHomeDirectory();

        Path libsPath = Paths.get(runtimeHomeDirectory,
                NameConvention.RUNTIME_PACKAGE_LIB_DIRECTORY);
        Path runtimePath = Paths.get(runtimeHomeDirectory,
                NameConvention.RUNTIME_PACKAGE_BIN_DIRECTORY,
                NameConvention.RUNTIME_JAR_FILE_PREFIX + "." + FileExtension.JAR.value());

        javaParams.setJdk(sdk);
        javaParams.getClassPath().add(libsPath.toString() + File.separator +  "*");
        javaParams.getClassPath().add(runtimePath.toString());
        javaParams.setMainClass(NameConvention.RUNTIME_LAUNCHER_CLASS);
        return javaParams;
    }
}
