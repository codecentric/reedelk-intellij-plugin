package com.reedelk.plugin.commons;

import com.intellij.execution.RunManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeComboManager {

    public static final String NO_RUNTIME_CONFIG_AVAILABLE = message("moduleBuilder.runtime.run.config.not.available.combo.default");

    private JComboBox<String> comboBox;

    public RuntimeComboManager(@NotNull JComboBox<String> comboBox, Project project) {
        configure(comboBox, project, Collections.emptyList());
        this.comboBox.setSelectedIndex(0);
    }

    public RuntimeComboManager(@NotNull JComboBox<String> comboBox, Project project, List<String> additionalItems, ItemListener itemListener) {
        configure(comboBox, project, additionalItems);
        this.comboBox.addItemListener(itemListener);
        this.comboBox.setSelectedIndex(0);
    }

    public String getRuntimeConfigName() {
        return (String) this.comboBox.getSelectedItem();
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        for (int i = 0; i < comboBox.getModel().getSize(); i++) {
            if (comboBox.getItemAt(i).equals(runtimeConfigName)) {
                comboBox.setSelectedItem(runtimeConfigName);
                return;
            }
        }
        // The Runtime config name is not valid (probably it has been cancelled)
        if (NO_RUNTIME_CONFIG_AVAILABLE.equals(runtimeConfigName)) {
            comboBox.addItem(runtimeConfigName);
            comboBox.setSelectedItem(runtimeConfigName);
        } else {
            String notFoundItem = runtimeConfigName + " (not found)";
            comboBox.addItem(notFoundItem);
            comboBox.setSelectedItem(notFoundItem);
        }
    }

    private void configure(@NotNull JComboBox<String> comboBox, Project project, List<String> additionalItems) {
        this.comboBox = comboBox;
        if (project != null) {
            RunConfigUtils.RuntimeRunConfiguration.type().ifPresent(runtimeRunConfigurationType ->
                    RunManager.getInstance(project).getConfigurationsList(runtimeRunConfigurationType)
                            .stream()
                            .map(configuration -> (RuntimeRunConfiguration) configuration)
                            .forEach(configuration -> comboBox.addItem(configuration.getName())));
        }
        additionalItems.forEach(comboBox::addItem);

        // By default we add a placeholder, to make clear to the user that there are no runtime configurations
        // defined in the project and that one should be defined.
        if (comboBox.getModel().getSize() == 0) {
            comboBox.addItem(NO_RUNTIME_CONFIG_AVAILABLE);
        }
    }
}
