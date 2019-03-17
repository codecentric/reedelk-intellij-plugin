package com.esb.plugin.runconfig.runtime;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.project.Project;

public class ESBRuntimeRunConfigurationBuilder {

    private String runtimeHomeDirectory;
    private String runtimeConfigName;

    private ESBRuntimeRunConfigurationBuilder() {
    }

    public static ESBRuntimeRunConfigurationBuilder build() {
        return new ESBRuntimeRunConfigurationBuilder();
    }

    public ESBRuntimeRunConfigurationBuilder withRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
        return this;
    }

    public ESBRuntimeRunConfigurationBuilder withRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
        return this;
    }

    public void add(Project project) {
        RunnerAndConfigurationSettings runConfigurationSettings = RunManager.getInstance(project).createConfiguration(runtimeConfigName, new ESBRuntimeRunConfigurationFactory(new ESBRuntimeRunConfigurationType()));
        ESBRuntimeRunConfiguration configuration = (ESBRuntimeRunConfiguration) runConfigurationSettings.getConfiguration();
        configuration.setRuntimeHomeDirectory(runtimeHomeDirectory);
        configuration.setName(runtimeConfigName);

        RunManager.getInstance(project).addConfiguration(runConfigurationSettings);
        RunManager.getInstance(project).setSelectedConfiguration(runConfigurationSettings);
    }
}
