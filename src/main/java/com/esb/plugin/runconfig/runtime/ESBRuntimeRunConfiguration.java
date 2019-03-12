package com.esb.plugin.runconfig.runtime;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESBRuntimeRunConfiguration extends RunConfigurationBase {

    private String vmOptions;
    private String runtimePort;
    private String runtimeHomeDirectory;

    protected ESBRuntimeRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<ESBRuntimeRunConfiguration> getConfigurationEditor() {
        return new RuntimeRunConfigurationSettings(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new ESBRuntimeRunCommandLine(this, environment);
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    public void setRuntimePort(String runtimePort) {
        this.runtimePort = runtimePort;
    }

    public void setRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public String getRuntimePort() {
        return runtimePort;
    }

    public String getRuntimeHomeDirectory() {
        return runtimeHomeDirectory;
    }

}
