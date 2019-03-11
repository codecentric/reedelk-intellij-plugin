package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.esb.plugin.service.ESBRuntime;
import com.esb.plugin.service.ESBRuntimeService;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;
import java.util.Collection;

public class ConfigureRuntimeStep extends ModuleWizardStep {

    private final ESBModuleBuilder moduleBuilder;
    private JComboBox<RuntimeItem> runtimeCombo;
    private JButton btnAddRuntime;
    private JPanel jPanel;

    public ConfigureRuntimeStep(WizardContext wizardContext, ESBModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;

        ESBRuntimeService runtimeService = ServiceManager.getService(ESBRuntimeService.class);
        Collection<ESBRuntime> definedRuntimes = runtimeService.listRuntimes();

        for (ESBRuntime runtime : definedRuntimes) {
            runtimeCombo.addItem(new RuntimeItem(runtime));
        }

        btnAddRuntime.addActionListener(e -> {
            AddRuntimeDialog dialog = new AddRuntimeDialog(jPanel, wizardContext, moduleBuilder);
            if(!dialog.showAndGet()) {
                return;
            }

            ESBRuntime runtime = dialog.getRuntime();
            ServiceManager.getService(ESBRuntimeService.class).addRuntime(runtime);
            addRuntimeAndUpdateCombo(runtime);
        });

    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {
        this.moduleBuilder.setRuntimeHome("test");
    }

    private void addRuntimeAndUpdateCombo(final ESBRuntime selected) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        RuntimeItem newItem = new RuntimeItem(selected);
        runtimeCombo.addItem(newItem);
        runtimeCombo.setSelectedItem(newItem);
    }

    private static class RuntimeItem {

        private final ESBRuntime runtime;

        RuntimeItem(ESBRuntime runtime) {
            this.runtime = runtime;
        }

        public ESBRuntime getRuntime() {
            return runtime;
        }

        @Override
        public String toString() {
            return runtime.name;
        }
    }
}
