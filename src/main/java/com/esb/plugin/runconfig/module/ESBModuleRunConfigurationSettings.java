package com.esb.plugin.runconfig.module;

import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.esb.plugin.ui.RuntimeComboManager;
import com.intellij.application.options.ModuleDescriptionsComboBox;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.esb.plugin.module.wizard.step.ConfigureRuntimeStep.*;
import static com.intellij.uiDesigner.core.GridConstraints.*;

public class ESBModuleRunConfigurationSettings extends SettingsEditor<ESBModuleRunConfiguration> {

    // Make Module Selectable
    // Make Runtime Selectable
    private JPanel jPanel;
    private JComboBox<RuntimeItem> runtimeCombo;
    private ModuleDescriptionsComboBox moduleComboBox;
    private RuntimeComboManager runtimeComboManager;


    public ESBModuleRunConfigurationSettings(@NotNull Project project) {
        moduleComboBox = new ModuleDescriptionsComboBox();
        moduleComboBox.setAllModulesFromProject(project);


        JPanel moduleChooserPanel = UI.PanelFactory.panel(moduleComboBox).
                withComment("Choose the ESB Module this run configuration will be applied to")
                .createPanel();

        jPanel.add(moduleChooserPanel, MODULE_SELECTOR);


        runtimeComboManager = new RuntimeComboManager(runtimeCombo, project);
    }

    @Override
    protected void resetEditorFrom(@NotNull ESBModuleRunConfiguration configuration) {
        String module = configuration.getModuleName();
        if (module != null) {
            Project project = configuration.getProject();
            Module moduleByName = ModuleManager.getInstance(project).findModuleByName(module);
            if (moduleByName != null) moduleComboBox.setSelectedModule(moduleByName);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull ESBModuleRunConfiguration configuration) throws ConfigurationException {
        Module selectedModule = moduleComboBox.getSelectedModule();
        if (selectedModule != null) configuration.setModule(selectedModule.getName());
    }


    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }

    private static final GridConstraints MODULE_SELECTOR  =
            new GridConstraints(1, 1, 1, 1,
                    ANCHOR_WEST,
                    FILL_HORIZONTAL,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));

}
