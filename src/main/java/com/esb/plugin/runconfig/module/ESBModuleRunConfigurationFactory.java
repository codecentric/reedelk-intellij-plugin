package com.esb.plugin.runconfig.module;

import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ESBModuleRunConfigurationFactory extends ConfigurationFactory {

    private static final String FACTORY_NAME = "ESB Module Run Configuration Factory";


    protected ESBModuleRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ESBModuleRunConfiguration(project, this, "ESB Module Run Configuration");
    }

    @NotNull
    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
