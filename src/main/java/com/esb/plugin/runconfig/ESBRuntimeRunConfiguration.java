package com.esb.plugin.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESBRuntimeRunConfiguration extends RunConfigurationBase {

    protected ESBRuntimeRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RuntimeRunConfigurationSettings();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new ESBRunCommandLine(environment);
    }

    class ESBRunCommandLine extends JavaCommandLineState {

        protected ESBRunCommandLine(@NotNull ExecutionEnvironment environment) {
            super(environment);
        }

        @Override
        protected JavaParameters createJavaParameters() throws ExecutionException {
            JavaParameters javaParams = new JavaParameters();
            Project project = getProject();
            ProjectRootManager manager = ProjectRootManager.getInstance(project);
            javaParams.setJdk(manager.getProjectSdk());
            javaParams.setJarPath("/Users/lorenzo/Desktop/esb-project/tools/release-tool/runtime-1.0.0-SNAPSHOT/bin/runtime-1.0.0-SNAPSHOT.jar");
            return javaParams;
        }
    }
}
