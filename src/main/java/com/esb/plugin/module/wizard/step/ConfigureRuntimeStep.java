package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.esb.plugin.service.application.runtime.ESBRuntime;
import com.esb.plugin.service.application.runtime.ESBRuntimeService;
import com.esb.plugin.ui.RuntimeComboManager;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public class ConfigureRuntimeStep extends ModuleWizardStep implements Disposable {

    private ESBModuleBuilder moduleBuilder;

    private JPanel jPanel;
    private JButton btnAddRuntime;
    private JComboBox<RuntimeItem> runtimeCombo;

    private RuntimeComboManager runtimeComboManager;

    public ConfigureRuntimeStep(WizardContext wizardContext, ESBModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;

        runtimeComboManager = new RuntimeComboManager(runtimeCombo, ServiceManager.getService(ESBRuntimeService.class));

        btnAddRuntime.addActionListener(e -> {
            AddRuntimeDialog dialog = new AddRuntimeDialog(jPanel, wizardContext, moduleBuilder);
            if(!dialog.showAndGet()) {
                return;
            }
            runtimeComboManager.addAndSelect(dialog.getRuntime());
        });
    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {
        moduleBuilder.setRuntime(runtimeComboManager.getSelected());
    }

    @Override
    public void dispose() {

    }

    public static class RuntimeItem {

        private final ESBRuntime runtime;

        public RuntimeItem(ESBRuntime runtime) {
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
