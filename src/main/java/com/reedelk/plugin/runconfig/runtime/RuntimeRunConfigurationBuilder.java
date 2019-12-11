package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.RunManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.RunConfigUtils;

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
        RunConfigUtils.RuntimeRunConfiguration.create(project, runtimeConfigName).ifPresent(runnerAndConfigurationSettings -> {
            RuntimeRunConfiguration configuration = (RuntimeRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
            configuration.setRuntimeHomeDirectory(runtimeHomeDirectory);
            configuration.setName(runtimeConfigName);

            RunManager.getInstance(project).addConfiguration(runnerAndConfigurationSettings);
            RunManager.getInstance(project).setSelectedConfiguration(runnerAndConfigurationSettings);
        });
    }
}
