package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.reedelk.plugin.commons.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RuntimeRunConfigurationType implements ConfigurationType {
    @NotNull
    @Override
    public String getDisplayName() {
        return "Runtime Run Config";
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return "Launches Reedelk Runtime";
    }

    @Override
    public Icon getIcon() {
        return Icons.Runtime;
    }

    @NotNull
    @Override
    public String getId() {
        return "ESB_RUNTIME_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{
                new RuntimeRunConfigurationFactory(this)
        };
    }
}
