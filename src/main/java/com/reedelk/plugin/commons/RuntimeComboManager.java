package com.reedelk.plugin.commons;

import com.intellij.execution.RunManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeComboManager {

    public static final String NO_RUNTIME_CONFIG_AVAILABLE = message("moduleBuilder.runtime.run.config.not.available.combo.default");

    private String runtimeConfigName;
    private JComboBox<String> comboBox;

    public RuntimeComboManager(@NotNull JComboBox<String> comboBox, Project project) {
        configure(comboBox, project, Collections.emptyList());
        this.comboBox.setSelectedIndex(0);
        this.runtimeConfigName = (String) this.comboBox.getSelectedItem();
    }

    public RuntimeComboManager(@NotNull JComboBox<String> comboBox, Project project, List<String> additionalItems, ItemListener itemListener) {
        configure(comboBox, project, additionalItems);
        this.comboBox.addItemListener(itemListener);
        this.comboBox.setSelectedIndex(0);
        this.runtimeConfigName = (String) this.comboBox.getSelectedItem();
    }

    public String getRuntimeConfigName() {
        return runtimeConfigName;
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        comboBox.setSelectedItem(runtimeConfigName);
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

        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                this.runtimeConfigName = (String) event.getItem();
            }
        });
    }
}
