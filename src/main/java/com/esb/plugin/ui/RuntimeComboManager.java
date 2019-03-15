package com.esb.plugin.ui;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.esb.plugin.runconfig.module.ESBModuleRunConfigurationType;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfigurationType;
import com.esb.plugin.service.application.runtime.ESBRuntime;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.esb.plugin.module.wizard.step.ConfigureRuntimeStep.RuntimeItem;

public class RuntimeComboManager {

    private ESBRuntime runtime;
    private JComboBox<RuntimeItem> comboBox;


    public RuntimeComboManager(@NotNull JComboBox<RuntimeItem> comboBox, Project project) {
        this.comboBox = comboBox;

        if (project != null) {
            List<RunConfiguration> configurationsList = RunManager.getInstance(project).getConfigurationsList(new ESBRuntimeRunConfigurationType());

            configurationsList
                    .stream()
                    .map(configuration -> (ESBRuntimeRunConfiguration) configuration)
                    .forEach(configuration -> {
                        ESBRuntime runtime = new ESBRuntime();
                        runtime.name = configuration.getName();
                        runtime.path = configuration.getRuntimeHomeDirectory();
                        comboBox.addItem(new RuntimeItem(runtime));
                    });
        }


        this.comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                RuntimeItem item = (RuntimeItem) event.getItem();
                this.runtime = item.getRuntime();
            }
        });

    }

    public ESBRuntime getSelected() {
        return runtime;
    }

}
