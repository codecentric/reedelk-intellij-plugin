package de.codecentric.reedelk.plugin.runconfig.module;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModuleRunConfigurationType implements ConfigurationType {

    @NotNull
    @Override
    public String getDisplayName() {
        return ReedelkBundle.message("module.run.config.name");
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return "Install/Update Reedelk Module";
    }

    @Override
    public Icon getIcon() {
        return Icons.Module;
    }

    @NotNull
    @Override
    public String getId() {
        return ReedelkBundle.message("module.run.config.id");
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new ModuleRunConfigurationFactory(this)};
    }
}
