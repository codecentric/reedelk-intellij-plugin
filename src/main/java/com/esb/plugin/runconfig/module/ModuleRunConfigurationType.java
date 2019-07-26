package com.esb.plugin.runconfig.module;

import com.esb.plugin.commons.Icons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModuleRunConfigurationType implements ConfigurationType {
    @NotNull
    @Override
    public String getDisplayName() {
        return "ESB Module Run Config";
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return "Install/Update ESB Module";
    }

    @Override
    public Icon getIcon() {
        return Icons.Module;
    }

    @NotNull
    @Override
    public String getId() {
        return "ESB_MODULE_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new ModuleRunConfigurationFactory(this)};
    }
}