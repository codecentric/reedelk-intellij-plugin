package com.reedelk.plugin.runconfig.module;

import com.intellij.application.options.ModuleDescriptionsComboBox;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.UI;
import com.reedelk.plugin.commons.RuntimeComboManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class ModuleRunConfigurationSettings extends SettingsEditor<ModuleRunConfiguration> {

    private JPanel jPanel;
    private JComboBox<String> runtimeCombo;
    private RuntimeComboManager runtimeComboManager;
    private ModuleDescriptionsComboBox moduleComboBox;


    public ModuleRunConfigurationSettings(@NotNull Project project) {
        moduleComboBox = new ModuleDescriptionsComboBox();
        moduleComboBox.setAllModulesFromProject(project);


        JPanel moduleChooserPanel = UI.PanelFactory.panel(moduleComboBox).
                withComment("Choose the ESB Module this run configuration will be applied to")
                .createPanel();

        jPanel.add(moduleChooserPanel, MODULE_SELECTOR);


        runtimeComboManager = new RuntimeComboManager(runtimeCombo, project);
    }

    @Override
    protected void resetEditorFrom(@NotNull ModuleRunConfiguration configuration) {
        String module = configuration.getModuleName();
        if (module != null) {
            Project project = configuration.getProject();
            Module moduleByName = ModuleManager.getInstance(project).findModuleByName(module);
            if (moduleByName != null) moduleComboBox.setSelectedModule(moduleByName);
        }

        String runtimeConfigName = configuration.getRuntimeConfigName();
        if (runtimeConfigName != null) {
            runtimeComboManager.setRuntimeConfigName(runtimeConfigName);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull ModuleRunConfiguration configuration) throws ConfigurationException {
        Module selectedModule = moduleComboBox.getSelectedModule();
        if (selectedModule != null) configuration.setModule(selectedModule.getName());

        if (runtimeComboManager.getRuntimeConfigName() != null) {
            configuration.setRuntimeConfigName(runtimeComboManager.getRuntimeConfigName());
        }
    }


    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }

    private static final GridConstraints MODULE_SELECTOR =
            new GridConstraints(1, 1, 1, 1,
                    ANCHOR_WEST,
                    FILL_HORIZONTAL,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));

}
