package com.esb.plugin.runconfig.runtime;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.project.Project;

public class RuntimeRunConfigurationBuilder {

    private String runtimeHomeDirectory;
    private String runtimeConfigName;

    private RuntimeRunConfigurationBuilder() {
    }

    public static RuntimeRunConfigurationBuilder build() {
        return new RuntimeRunConfigurationBuilder();
    }

    public RuntimeRunConfigurationBuilder withRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
        return this;
    }

    public RuntimeRunConfigurationBuilder withRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
        return this;
    }

    public void add(Project project) {
        RunnerAndConfigurationSettings runConfigurationSettings = RunManager.getInstance(project).createConfiguration(runtimeConfigName, new RuntimeRunConfigurationFactory(new RuntimeRunConfigurationType()));
        RuntimeRunConfiguration configuration = (RuntimeRunConfiguration) runConfigurationSettings.getConfiguration();
        configuration.setRuntimeHomeDirectory(runtimeHomeDirectory);
        configuration.setName(runtimeConfigName);

        RunManager.getInstance(project).addConfiguration(runConfigurationSettings);
        RunManager.getInstance(project).setSelectedConfiguration(runConfigurationSettings);
    }
}
