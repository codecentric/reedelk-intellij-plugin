package com.reedelk.plugin.runconfig.module;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ModuleRunConfigurationFactory extends ConfigurationFactory {

    private static final String FACTORY_NAME = "Module Run Configuration Factory";
    private static final String FACTORY_ID = "REEDELK_MODULE_RUN_CONFIGURATION_FACTORY";

    public ModuleRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ModuleRunConfiguration(project, this, "Module Run Configuration");
    }

    @NotNull
    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @NotNull
    @Override
    public String getId() {
        return FACTORY_ID;
    }
}
