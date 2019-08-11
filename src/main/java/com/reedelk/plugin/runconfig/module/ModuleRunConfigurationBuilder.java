package com.reedelk.plugin.runconfig.module;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.project.Project;

public class ModuleRunConfigurationBuilder {

    private String moduleName;
    private String runtimeConfigName;

    private ModuleRunConfigurationBuilder() {
    }

    public static ModuleRunConfigurationBuilder build() {
        return new ModuleRunConfigurationBuilder();
    }

    public ModuleRunConfigurationBuilder withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ModuleRunConfigurationBuilder withRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
        return this;
    }

    public void add(Project project) {
        RunnerAndConfigurationSettings moduleConfigurationSettings =
                RunManager.getInstance(project).createConfiguration(moduleName,
                        new ModuleRunConfigurationFactory(new ModuleRunConfigurationType()));

        ModuleRunConfiguration moduleRunConfiguration =
                (ModuleRunConfiguration) moduleConfigurationSettings.getConfiguration();
        moduleRunConfiguration.setModule(moduleName);
        moduleRunConfiguration.setRuntimeConfigName(runtimeConfigName);
        RunManager.getInstance(project).addConfiguration(moduleConfigurationSettings);
    }
}
