package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.esb.plugin.service.ESBRuntime;
import com.esb.plugin.service.ESBRuntimeService;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;
import java.util.Collection;

public class ConfigureRuntimeStep extends ModuleWizardStep {
    private final WizardContext wizardContext;
    private final ESBModuleBuilder moduleBuilder;
    private JComboBox<ESBRuntime> runtimeCombo;
    private JButton btnAddRuntime;
    private JPanel jPanel;

    public ConfigureRuntimeStep(WizardContext wizardContext, ESBModuleBuilder moduleBuilder) {
        this.wizardContext = wizardContext;
        this.moduleBuilder = moduleBuilder;

        ESBRuntimeService runtimeService = ServiceManager.getService(ESBRuntimeService.class);
        Collection<ESBRuntime> definedRuntimes = runtimeService.listRuntimes();

        for (ESBRuntime runtime : definedRuntimes) {
            runtimeCombo.addItem(runtime);
        }

    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {
        this.moduleBuilder.setRuntimeHome("test");
    }
}
