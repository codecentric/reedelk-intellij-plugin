package com.esb.plugin.ui;

import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfigurationType;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class RuntimeComboManager {

    private String runtimeConfigName;
    private JComboBox<String> comboBox;

    public RuntimeComboManager(@NotNull JComboBox<String> comboBox, Project project) {
        this.comboBox = comboBox;

        if (project != null) {
            List<RunConfiguration> configurationsList = RunManager.getInstance(project).getConfigurationsList(new ESBRuntimeRunConfigurationType());

            configurationsList
                    .stream()
                    .map(configuration -> (ESBRuntimeRunConfiguration) configuration)
                    .forEach(configuration -> {
                        comboBox.addItem(configuration.getName());
                    });
        }


        this.comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                this.runtimeConfigName = (String) event.getItem();
            }
        });

    }

    public String getRuntimeConfigName() {
        return runtimeConfigName;
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        comboBox.setSelectedItem(runtimeConfigName);
    }
}
