package com.esb.plugin.runconfig.module;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.project.Project;

public class ESBModuleRunConfigurationBuilder {

    private String moduleName;
    private String runtimeConfigName;

    private ESBModuleRunConfigurationBuilder() {
    }

    public static ESBModuleRunConfigurationBuilder build() {
        return new ESBModuleRunConfigurationBuilder();
    }

    public ESBModuleRunConfigurationBuilder withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ESBModuleRunConfigurationBuilder withRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
        return this;
    }

    public void add(Project project) {
        RunnerAndConfigurationSettings moduleConfigurationSettings =
                RunManager.getInstance(project).createConfiguration(moduleName,
                new ESBModuleRunConfigurationFactory(new ESBModuleRunConfigurationType()));

        ESBModuleRunConfiguration esbModuleRunConfiguration =
                (ESBModuleRunConfiguration) moduleConfigurationSettings.getConfiguration();
        esbModuleRunConfiguration.setModule(moduleName);
        esbModuleRunConfiguration.setRuntimeConfigName(runtimeConfigName);
        RunManager.getInstance(project).addConfiguration(moduleConfigurationSettings);
    }
}
