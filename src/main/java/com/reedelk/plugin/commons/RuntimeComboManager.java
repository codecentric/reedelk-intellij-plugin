package com.reedelk.plugin.commons;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

public class RuntimeComboManager {

    private String runtimeConfigName;
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
        return runtimeConfigName;
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        comboBox.setSelectedItem(runtimeConfigName);
    }

    private void configure(@NotNull JComboBox<String> comboBox, Project project, List<String> additionalItems) {
        this.comboBox = comboBox;
        if (project != null) {
            List<RunConfiguration> runtimeConfigurations = RunManager.getInstance(project).getConfigurationsList(new RuntimeRunConfigurationType());
            runtimeConfigurations.stream()
                    .map(configuration -> (RuntimeRunConfiguration) configuration)
                    .forEach(configuration -> comboBox.addItem(configuration.getName()));
        }
        additionalItems.forEach(comboBox::addItem);
        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                this.runtimeConfigName = (String) event.getItem();
            }
        });
    }
}
