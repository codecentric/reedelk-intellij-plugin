package de.codecentric.reedelk.plugin.runconfig.module;

import com.intellij.execution.RunManager;
import com.intellij.openapi.project.Project;
import de.codecentric.reedelk.plugin.commons.RunConfigUtils;

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
        RunConfigUtils.ModuleRunConfiguration.create(project, moduleName).ifPresent(runnerAndConfigurationSettings -> {
            ModuleRunConfiguration moduleRunConfiguration =
                    (ModuleRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
            moduleRunConfiguration.setModule(moduleName);
            moduleRunConfiguration.setRuntimeConfigName(runtimeConfigName);
            RunManager.getInstance(project).addConfiguration(runnerAndConfigurationSettings);
        });
    }
}
