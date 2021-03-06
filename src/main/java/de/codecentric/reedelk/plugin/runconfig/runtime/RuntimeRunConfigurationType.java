package de.codecentric.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RuntimeRunConfigurationType implements ConfigurationType {

    @NotNull
    @Override
    public String getDisplayName() {
        return ReedelkBundle.message("runtime.run.config.name");
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
        return ReedelkBundle.message("runtime.run.config.id");
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{
                new RuntimeRunConfigurationFactory(this)
        };
    }
}