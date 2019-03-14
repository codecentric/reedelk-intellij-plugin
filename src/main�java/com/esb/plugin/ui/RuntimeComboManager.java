package com.esb.plugin.ui;

import com.esb.plugin.service.application.runtime.ESBRuntime;
import com.esb.plugin.service.application.runtime.ESBRuntimeService;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;

import static com.esb.plugin.module.wizard.step.ConfigureRuntimeStep.RuntimeItem;

public class RuntimeComboManager {

    private ESBRuntime runtime;
    private JComboBox<RuntimeItem> comboBox;
    private final ESBRuntimeService service;

    public RuntimeComboManager(@NotNull JComboBox<RuntimeItem> comboBox, @NotNull ESBRuntimeService service) {
        this.comboBox = comboBox;
        this.service = service;

        this.service.listRuntimes()
                .forEach(esbRuntime -> comboBox.addItem(new RuntimeItem(esbRuntime)));

        this.comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                RuntimeItem item = (RuntimeItem) event.getItem();
                this.runtime = item.getRuntime();
            }
        });

    }

    public void addAndSelect(@NotNull ESBRuntime runtime) {
        if (!service.contains(runtime)) service.addRuntime(runtime);

        ApplicationManager.getApplication().assertIsDispatchThread();
        RuntimeItem newItem = new RuntimeItem(runtime);
        comboBox.addItem(newItem);
        comboBox.setSelectedItem(newItem);
    }

    public ESBRuntime getSelected() {
        return runtime;
    }
}
