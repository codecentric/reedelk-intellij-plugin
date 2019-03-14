package com.esb.plugin.runconfig.runtime;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ESBRuntimeRunConfigurationFactory extends ConfigurationFactory {

    private static final String FACTORY_NAME = "ESB Runtime Run Configuration Factory";

    public ESBRuntimeRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ESBRuntimeRunConfiguration(project, this, "ESB Runtime Run Configuration");
    }

    @NotNull
    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
