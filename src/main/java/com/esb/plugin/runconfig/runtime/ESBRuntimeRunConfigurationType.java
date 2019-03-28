package com.esb.plugin.runconfig.runtime;

import com.esb.plugin.commons.ESBIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESBRuntimeRunConfigurationType implements ConfigurationType {
    @NotNull
    @Override
    public String getDisplayName() {
        return "ESB Runtime Run Config";
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return "Launches an ESB Runtime";
    }

    @Override
    public Icon getIcon() {
        return ESBIcons.Runtime;
    }

    @NotNull
    @Override
    public String getId() {
        return "ESB_RUNTIME_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{
                new ESBRuntimeRunConfigurationFactory(this)
        };
    }
}
